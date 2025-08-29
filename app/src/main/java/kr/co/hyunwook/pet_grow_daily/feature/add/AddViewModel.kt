package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.Glide.init
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveAlbumRecordUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase
import kr.co.hyunwook.pet_grow_daily.util.formatDate
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getLongOrNull
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.io.IOException
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min
import androidx.core.net.toUri
import kr.co.hyunwook.pet_grow_daily.analytics.Analytics
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants

@HiltViewModel
class AddViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveAlbumRecordUseCase: SaveAlbumRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val analytics: Analytics


) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState = _uiState.asStateFlow()

    private val _saveDoneEvent = MutableSharedFlow<Boolean>()
    val saveDoneEvent: SharedFlow<Boolean> get() = _saveDoneEvent


    init {
        loadImages()
    }

    // 임시 파일 정리 함수
    private fun cleanupTempFile(uri: Uri) {
        try {
            if (uri.scheme == "file") {
                val file = java.io.File(uri.path ?: return)
                if (file.exists()) {
                    file.delete()
                }
            }
        } catch (e: Exception) {
            Log.e("AddViewModel", "임시 파일 정리 실패", e)
        }
    }

    fun uploadImageEvent(isPublic: Boolean) {
        analytics.track(
            EventConstants.UPLOAD_IMAGE_EVENT,
            mapOf(
                EventConstants.IS_PUBLIC_PROPERTY to isPublic
            )
        )
    }

    fun uploadAndSaveAlbumRecord(
        selectedImageUris: List<String>,
        content: String,
        isPublic: Boolean
    ) {
        viewModelScope.launch {
            val userId = getUserIdUseCase.invoke()

            try {
                val uris = selectedImageUris.map { it.toUri() }
                val uploadTasks = uris.mapIndexed { index, uri ->
                    async {
                        uploadImageToStorage(uri, userId.toString())
                    }
                }

                val imageUrls = uploadTasks.awaitAll()

                val albumRecord = AlbumRecord(
                    date = System.currentTimeMillis(),
                    content = content,
                    firstImage = imageUrls.getOrNull(0) ?: "",
                    secondImage = imageUrls.getOrNull(1) ?: "",
                    isPublic = isPublic
                )

                saveAlbumRecord(albumRecord)

                // 저장 완료 후 Analytics 이벤트 호출
                uploadImageEvent(isPublic = isPublic)
                _saveDoneEvent.emit(true)
            } catch (e: Exception) {
                Log.e("AddViewModel", "Error uploading/saving album record", e)
                _saveDoneEvent.emit(false)
            }
        }
    }

    private suspend fun saveAlbumRecord(albumRecord: AlbumRecord) {
        Log.d("HWO", "saveAlbumRecord -> $albumRecord")
        try {
            saveAlbumRecordUseCase(albumRecord)
        } catch (e: Exception) {
            throw e // 예외를 다시 던져서 상위에서 처리하도록 함
        }
    }

    private suspend fun uploadImageToStorage(uri: Uri, userId: String): String {
        var retryCount = 0
        val maxRetries = 3

        return try {
            val optimizedImageUri = optimizeImage(uri)
            val fileName = "album_${userId}_${System.currentTimeMillis()}.jpg"

            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child("albums")
                .child(fileName)

            // 재시도 로직 추가
            while (retryCount < maxRetries) {
                try {
                    // 업로드 태스크에 메타데이터 설정으로 안정성 향상
                    val metadata = com.google.firebase.storage.StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build()

                    val uploadTask = storageRef.putFile(optimizedImageUri, metadata)

                    // 업로드 진행률 모니터링으로 세션 유지
                    uploadTask.addOnProgressListener { taskSnapshot ->
                        val progress =
                            (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount)
                        Log.d("AddViewModel", "업로드 진행률: ${progress.toInt()}%")
                    }

                    // 업로드 완료 대기
                    uploadTask.await()

                    val downloadUrl = storageRef.downloadUrl.await().toString()

                    // 앱의 private cache 파일 삭제 (권한 문제 없음)
                    cleanupTempFile(optimizedImageUri)

                    return downloadUrl

                } catch (e: Exception) {
                    retryCount++
                    Log.w("AddViewModel", "업로드 시도 ${retryCount}/$maxRetries 실패: ${e.message}")

                    if (retryCount >= maxRetries) {
                        throw e
                    }

                    // 재시도 전 잠시 대기 (백오프 전략)
                    delay(1000L * retryCount)
                }
            }

            throw IOException("최대 재시도 횟수 초과")

        } catch (e: Exception) {
            Log.e("AddViewModel", "Firebase Storage 업로드 최종 실패", e)
            throw e
        }
    }

    fun reloadImages() {
        loadImages()
    }

    // 폴더 선택 시 호출
    fun selectFolder(folder: GalleryFolder) {
        _uiState.update {
            it.copy(
                selectedFolder = folder,
                currentView = AddViewType.IMAGE_GRID,
                isLoading = true
            )
        }
        loadImagesFromFolder(folder.bucketId)
    }

    // 폴더 목록으로 돌아가기
    fun navigateToFolderList() {
        _uiState.update {
            it.copy(
                selectedFolder = null,
                currentView = AddViewType.FOLDER_LIST,
                images = emptyList()
            )
        }
    }

    // 특정 폴더의 이미지들만 로드
    private fun loadImagesFromFolder(bucketId: Long) {
        viewModelScope.launch {
            val imagesList = mutableListOf<GalleryImage>()
            val contentResolver = context.contentResolver

            when (bucketId) {
                -1L -> { // 최근 항목
                    loadRecentImages(imagesList, contentResolver)
                }

                -2L -> { // 즐겨찾기
                    loadFavoriteImages(imagesList, contentResolver)
                }

                else -> { // 일반 폴더
                    loadImagesFromBucket(bucketId, imagesList, contentResolver)
                }
            }

            _uiState.update {
                it.copy(
                    images = imagesList,
                    isLoading = false
                )
            }
        }
    }

    private suspend fun loadRecentImages(
        imagesList: MutableList<GalleryImage>,
        contentResolver: android.content.ContentResolver
    ) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"
        val selectionArgs = arrayOf((thirtyDaysAgo / 1000).toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            processImageCursor(cursor, imagesList)
        }
    }

    private suspend fun loadFavoriteImages(
        imagesList: MutableList<GalleryImage>,
        contentResolver: android.content.ContentResolver
    ) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.IS_FAVORITE
            )

            val selection = "${MediaStore.Images.Media.IS_FAVORITE} = ?"
            val selectionArgs = arrayOf("1")
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                processImageCursor(cursor, imagesList)
            }
        }
    }

    private suspend fun loadImagesFromBucket(
        bucketId: Long,
        imagesList: MutableList<GalleryImage>,
        contentResolver: android.content.ContentResolver
    ) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        )

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(bucketId.toString())
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            processImageCursor(cursor, imagesList)
        }
    }

    private fun processImageCursor(
        cursor: android.database.Cursor,
        imagesList: MutableList<GalleryImage>
    ) {
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
        val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
        val bucketDisplayNameColumn =
            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val contentUri = ContentUris.withAppendedId(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                id
            )

            val dateTaken = cursor.getLongOrNull(dateTakenColumn)
            val dateAdded = cursor.getLong(dateAddedColumn) * 1000
            val timestamp = dateTaken ?: dateAdded
            val formattedDate = formatDate(timestamp)
            val bucketName = cursor.getString(bucketDisplayNameColumn)

            imagesList.add(
                GalleryImage(
                    id = id,
                    uri = contentUri,
                    date = formattedDate,
                    bucketName = bucketName
                )
            )
        }
    }

    private fun loadImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val imagesList = mutableListOf<GalleryImage>()
            val contentResolver = context.contentResolver

            val projection = mutableListOf<String>().apply {
                addAll(
                    arrayOf(
                        MediaStore.Images.Media._ID,
                        MediaStore.Images.Media.DISPLAY_NAME,
                        MediaStore.Images.Media.DATE_TAKEN,
                        MediaStore.Images.Media.DATE_ADDED,
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                        MediaStore.Images.Media.BUCKET_ID
                    )
                )

                // Android 10 이상에서만 즐겨찾기 정보 추가
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    add(MediaStore.Images.Media.IS_FAVORITE)
                }
            }

            val sortOrder =
                "${MediaStore.Images.Media.DATE_ADDED} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection.toTypedArray(),
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val bucketDisplayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

                // 즐겨찾기 컬럼은 Android 10+ 에서만 사용
                val isFavoriteColumn =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                        try {
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.IS_FAVORITE)
                        } catch (e: Exception) {
                            -1
                        }
                    } else {
                        -1
                    }

                val folders = mutableMapOf<Long, MutableList<GalleryImage>>()
                val favoriteImages = mutableListOf<GalleryImage>()
                val recentImages = mutableListOf<GalleryImage>()

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val dateTaken = cursor.getLongOrNull(dateTakenColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn) * 1000

                    val timestamp = dateTaken ?: dateAdded
                    val formattedDate = formatDate(timestamp)
                    val bucketName = cursor.getString(bucketDisplayNameColumn)
                    val bucketId = cursor.getLong(bucketIdColumn)

                    // 즐겨찾기 여부 확인
                    val isFavorite = if (isFavoriteColumn >= 0) {
                        cursor.getInt(isFavoriteColumn) == 1
                    } else {
                        false
                    }

                    val image = GalleryImage(
                        id = id,
                        uri = contentUri,
                        date = formattedDate,
                        bucketName = bucketName
                    )

                    imagesList.add(image)

                    // 일반 폴더별 분류
                    folders.getOrPut(bucketId) { mutableListOf() }.add(image)

                    // 즐겨찾기 이미지 수집
                    if (isFavorite) {
                        favoriteImages.add(image)
                    }

                    // 최신 항목 수집 (최근 30일)
                    val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
                    if (timestamp >= thirtyDaysAgo) {
                        recentImages.add(image)
                    }
                }

                val folderList = mutableListOf<GalleryFolder>()

                // 최신 항목 폴더 추가 (이미지가 있는 경우만)
                if (recentImages.isNotEmpty()) {
                    folderList.add(
                        GalleryFolder(
                            bucketId = -1L, // 특별한 ID로 구분
                            name = "최근 항목",
                            imageCount = recentImages.size,
                            thumbnailUri = recentImages.first().uri
                        )
                    )
                }

                // 즐겨찾기 폴더 추가 (Android 10+ 및 즐겨찾기가 있는 경우만)
                if (favoriteImages.isNotEmpty()) {
                    folderList.add(
                        GalleryFolder(
                            bucketId = -2L, // 특별한 ID로 구분
                            name = "즐겨찾기",
                            imageCount = favoriteImages.size,
                            thumbnailUri = favoriteImages.first().uri
                        )
                    )
                }

                // 일반 폴더들 추가
                folderList.addAll(
                    folders.map { (bucketId, images) ->
                        GalleryFolder(
                            bucketId = bucketId,
                            name = images.first().bucketName,
                            imageCount = images.size,
                            thumbnailUri = images.first().uri
                        )
                    }
                )

                _uiState.update {
                    it.copy(
                        images = imagesList,
                        folders = folderList,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun optimizeImage(uri: Uri): Uri {
        return withContext(Dispatchers.IO) {
            try {
                var originalSize = 0L
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        originalSize = inputStream.available().toLong()
                    }
                } catch (e: Exception) {
                }

                val inputStream = context.contentResolver.openInputStream(uri)
                var originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                // 1. Exif Orientation 읽어서 실제 회전 각도 계산
                val rotation = getExifRotation(context, uri)
                val rotatedBitmap = rotateBitmap(originalBitmap, rotation)
                if (rotatedBitmap != originalBitmap) {
                    originalBitmap.recycle()
                }
                val width = rotatedBitmap.width
                val height = rotatedBitmap.height

                val maxWidth = 2400
                val maxHeight = 3000

                val resizeBitmap = if (width > maxWidth || height > maxHeight) {
                    val ratio = min(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
                    val newWidth = (width * ratio).toInt()
                    val newHeight = (height * ratio).toInt()
                    Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)
                } else {
                    rotatedBitmap
                }

                // 앱의 private cache directory에 임시 파일 저장
                val fileName = "optimized_${System.currentTimeMillis()}.jpg"
                val tempFile = java.io.File(context.cacheDir, fileName)

                // 압축 품질 85%로 저장
                tempFile.outputStream().use { outputStream ->
                    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                }

                // 최종 파일 크기 확인
                var optimizedSize = tempFile.length()
                val compressionRatio = if (originalSize > 0) {
                    (100 - (optimizedSize * 100 / originalSize))
                } else 0

                if (resizeBitmap != rotatedBitmap) {
                    resizeBitmap.recycle()
                }
                rotatedBitmap.recycle()

                tempFile.toUri()
            } catch (e: Exception) {
                Log.e("AddViewModel", "이미지 최적화 실패", e)
                uri // 실패 시 원본 반환
            }
        }
    }

    // Exif에서 회전 각도 추출
    private fun getExifRotation(context: Context, uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                when (exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } ?: 0
        } catch (e: Exception) {
            0
        }
    }

    // Bitmap 회전 함수
    private fun rotateBitmap(original: Bitmap, rotationDegrees: Int): Bitmap {
        if (rotationDegrees == 0) return original
        val matrix = android.graphics.Matrix()
        matrix.postRotate(rotationDegrees.toFloat())
        return Bitmap.createBitmap(original, 0, 0, original.width, original.height, matrix, true)
    }

    private fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // 월은 0부터 시작하므로 +1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "${year}년 ${month}월 ${day}일"
    }
}

data class GalleryImage(
    val id: Long,
    val uri: Uri,
    val date: String,
    val bucketName: String = "" // 폴더명 추가
)

data class GalleryFolder(
    val bucketId: Long,
    val name: String,
    val imageCount: Int,
    val thumbnailUri: Uri? = null // 대표 이미지
)

data class AddUiState(
    val images: List<GalleryImage> = emptyList(),
    val folders: List<GalleryFolder> = emptyList(),
    val selectedFolder: GalleryFolder? = null,
    val currentView: AddViewType = AddViewType.FOLDER_LIST,
    val isLoading: Boolean = false
)

enum class AddViewType {
    FOLDER_LIST, // 폴더 목록 보기
    IMAGE_GRID   // 선택된 폴더의 이미지 그리드 보기
}

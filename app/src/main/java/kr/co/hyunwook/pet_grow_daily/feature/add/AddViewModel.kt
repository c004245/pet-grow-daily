package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.Glide.init
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase

@HiltViewModel
class AddViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveAlbumRecordUseCase: SaveAlbumRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase


) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState = _uiState.asStateFlow()

    private val _saveDoneEvent = MutableSharedFlow<Boolean>()
    val saveDoneEvent: SharedFlow<Boolean> get() = _saveDoneEvent


    init {
        loadImages()
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
                _saveDoneEvent.emit(true)
            } catch (e: Exception) {
                _saveDoneEvent.emit(false)
            }
        }
    }

    fun saveAlbumRecord(albumRecord: AlbumRecord) {
        Log.d("HWO", "saveAlbumRecord -> $albumRecord")
        viewModelScope.launch {
            try {
                saveAlbumRecordUseCase(albumRecord)
            } catch (e: Exception) {
                _saveDoneEvent.emit(false)
            }
        }
    }

    fun reloadImages() {
        loadImages()
    }




    private suspend fun uploadImageToStorage(uri: Uri, userId: String): String {
        return try {
            val optimizedImageUri = optimizeImage(uri)
            val fileName = "album_${userId}_${System.currentTimeMillis()}.jpg"

            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child("albums")
                .child(fileName)

            storageRef.putFile(optimizedImageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            context.contentResolver.delete(optimizedImageUri, null, null)

            downloadUrl
        } catch (e: Exception) {
            throw e
        }
    }
    private fun loadImages() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val imagesList = mutableListOf<GalleryImage>()
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.DATE_ADDED
            )

            val sortOrder =
                "${MediaStore.Images.Media.DATE_ADDED} DESC, ${MediaStore.Images.Media.DATE_ADDED} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

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
                    imagesList.add(GalleryImage(id = id, uri = contentUri, date = formattedDate))
                }
            }

            _uiState.update { it.copy(images = imagesList, isLoading = false) }

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
                
                val fileName = "optimized_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                
                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw IOException("Failed to create media store record")
                    
                // 압축 품질 85%로 저장
                context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                } ?: throw IOException("Failed to open output stream")
                
                // 최종 파일 크기 확인
                var optimizedSize = 0L
                try {
                    context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                        optimizedSize = inputStream.available().toLong()
                    }
                    val compressionRatio = if (originalSize > 0) {
                        (100 - (optimizedSize * 100 / originalSize))
                    } else 0
                } catch (e: Exception) {
                }

                if (resizeBitmap != rotatedBitmap) {
                    resizeBitmap.recycle()
                }
                rotatedBitmap.recycle()
                // originalBitmap은 위에서 이미 recycle됨 또는 rotatedBitmap와 동일
                
                imageUri
            } catch (e: Exception) {
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
    val date: String
)

data class AddUiState(
    val images: List<GalleryImage> = emptyList(),
    val isLoading: Boolean = false
)


package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.Glide.init
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveAlbumRecordUseCase
import kr.co.hyunwook.pet_grow_daily.util.formatDate
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import java.util.Calendar
import javax.inject.Inject
import androidx.core.database.getLongOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class AddViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val saveAlbumRecordUseCase: SaveAlbumRecordUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState = _uiState.asStateFlow()

    private val _saveDoneEvent = MutableSharedFlow<Boolean>()
    val saveDoneEvent: SharedFlow<Boolean> get() = _saveDoneEvent



    init {
        loadImages()
    }

    fun uploadAndSaveAlbumRecord(
        selectedImageUris: List<String>,
        content: String
    ) {
        viewModelScope.launch {
            try {
                val uris = selectedImageUris.map { Uri.parse(it) }

                val uploadTasks = uris.mapIndexed { index, uri ->
                    async {
                        uploadImageToStorage(uri, index)
                    }
                }

                val imageUrls = uploadTasks.awaitAll()
                // AlbumRecord 생성 및 저장
                val albumRecord = AlbumRecord(
                    date = System.currentTimeMillis(),
                    content = content,
                    firstImage = imageUrls.getOrNull(0) ?: "",
                    secondImage = imageUrls.getOrNull(1) ?: ""
                )

                // 기존 저장 함수 호출
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



    private suspend fun uploadImageToStorage(uri: Uri, index: Int): String {
        return try {
            val fileName = "image_${System.currentTimeMillis()}_$index.jpg"
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child("albums")
                .child(fileName)

            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            Log.e("Firebase", "이미지 업로드 실패: ${e.message}", e)
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

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC, ${MediaStore.Images.Media.DATE_ADDED} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

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


package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.Glide.init
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import java.util.Calendar
import javax.inject.Inject
import androidx.core.database.getLongOrNull
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class AddViewModel @Inject constructor(
    @ApplicationContext private val context: Context
): ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadImages()
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


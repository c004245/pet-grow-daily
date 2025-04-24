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
import javax.inject.Inject
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
                MediaStore.Images.Media.DISPLAY_NAME
            )

            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    imagesList.add(GalleryImage(id = id, uri = contentUri))
                }
            }

            _uiState.update { it.copy(images = imagesList, isLoading = false) }

        }

    }



}


data class GalleryImage(
    val id: Long,
    val uri: Uri
)

data class AddUiState(
    val images: List<GalleryImage> = emptyList(),
    val isLoading: Boolean = false
)


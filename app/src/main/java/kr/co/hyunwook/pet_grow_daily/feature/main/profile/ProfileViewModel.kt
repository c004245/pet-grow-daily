package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import com.google.firebase.storage.FirebaseStorage
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SavePetProfileUseCase
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min
import androidx.lifecycle.viewModelScope

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savePetProfileUseCase: SavePetProfileUseCase
) : ViewModel() {


    private val _saveProfileEvent = MutableSharedFlow<Boolean>()
    val saveProfileEvent: SharedFlow<Boolean> get() = _saveProfileEvent

    fun saveProfile(name: String, imageUrl: String?) {
        viewModelScope.launch {
            try {
                val profileImageUrl = if (imageUrl != null) {
                    uploadProfileImageToStorage(Uri.parse(imageUrl))
                } else {
                    ""
                }
                val profile = PetProfile(
                    name = name,
                    profileImageUrl = profileImageUrl
                )

                savePetProfileUseCase(profile)
                _saveProfileEvent.emit(true)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "프로필 저장 실패: ${e.message}", e)
                _saveProfileEvent.emit(false)
            }
        }
    }

    private suspend fun uploadProfileImageToStorage(uri: Uri): String {
        return try {
            // 이미지 최적화
            val optimizedImageUri = optimizeImage(uri)

            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val storageRef = FirebaseStorage.getInstance().reference
                .child("users")
                .child("profiles")
                .child(fileName)

            storageRef.putFile(optimizedImageUri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()

            // 임시 파일 삭제
            context.contentResolver.delete(optimizedImageUri, null, null)

            downloadUrl
        } catch (e: Exception) {
            Log.e("Firebase", "프로필 이미지 업로드 실패: ${e.message}", e)
            throw e
        }
    }

    private suspend fun optimizeImage(uri: Uri): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val width = originalBitmap.width
                val height = originalBitmap.height

                // 프로필 이미지는 더 작게 최적화
                val maxSize = 800
                val resizeBitmap = if (width > maxSize || height > maxSize) {
                    val ratio = min(maxSize.toFloat() / width, maxSize.toFloat() / height)
                    val newWidth = (width * ratio).toInt()
                    val newHeight = (height * ratio).toInt()
                    Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                } else {
                    originalBitmap
                }
                val fileName = "profile_temp_${System.currentTimeMillis()}.jpg"
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
                ) ?: throw IOException("Failed to create media store record")

                context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                } ?: throw IOException("Failed to open output stream")

                if (resizeBitmap != originalBitmap) {
                    resizeBitmap.recycle()
                }
                originalBitmap.recycle()

                imageUri
            } catch (e: Exception) {
                uri // 실패 시 원본 반환
            }
        }
    }
}

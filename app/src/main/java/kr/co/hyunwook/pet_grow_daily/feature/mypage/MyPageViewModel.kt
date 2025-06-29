package kr.co.hyunwook.pet_grow_daily.feature.mypage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetPetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SavePetProfileUseCase
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class MyPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPetProfileUseCase: GetPetProfileUseCase,
    private val savePetProfileUseCase: SavePetProfileUseCase
) : ViewModel() {

    val petProfile: StateFlow<PetProfile?> = flow {
        emit(Unit)
    }.flatMapLatest {
        getPetProfileUseCase()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateProfileImage(imageUri: String) {
        viewModelScope.launch {
            try {
                val currentProfile = petProfile.value

                currentProfile?.profileImageUrl?.let { oldUrl ->
                    if (oldUrl.isNotEmpty() && oldUrl.contains("firebase")) {
                        deleteOldProfileImage(oldUrl)
                    }
                }

                val profileImageUrl = uploadProfileImageToStorage(Uri.parse(imageUri))

                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(profileImageUrl = profileImageUrl)
                    savePetProfileUseCase(updatedProfile)
                } else {
                    val newProfile = PetProfile(
                        name = "사용자",
                        profileImageUrl = profileImageUrl
                    )
                    savePetProfileUseCase(newProfile)
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "프로필 이미지 업데이트 실패: ${e.message}", e)
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

    private suspend fun deleteOldProfileImage(oldImageUrl: String) {
        try {
            // Firebase Storage URL에서 파일 경로 추출하여 삭제
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
            storageRef.delete().await()
            Log.d("MyPageViewModel", "기존 프로필 이미지 삭제 완료")
        } catch (e: Exception) {
            Log.e("MyPageViewModel", "기존 프로필 이미지 삭제 실패: ${e.message}", e)
        }
    }

    private suspend fun optimizeImage(uri: Uri): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val rotatedBitmap = getRotatedBitmap(uri, originalBitmap)

                val width = originalBitmap.width
                val height = originalBitmap.height

                val maxSize = 1920
                val resizeBitmap = if (width > maxSize || height > maxSize) {
                    val ratio = min(maxSize.toFloat() / width, maxSize.toFloat() / height)
                    val newWidth = (width * ratio).toInt()
                    val newHeight = (height * ratio).toInt()
                    Bitmap.createScaledBitmap(rotatedBitmap, newWidth, newHeight, true)
                } else {
                    rotatedBitmap
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
                    resizeBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
                } ?: throw IOException("Failed to open output stream")

                if (resizeBitmap != rotatedBitmap) {
                    resizeBitmap.recycle()
                }
                if (rotatedBitmap != originalBitmap) {
                    rotatedBitmap.recycle()
                }
                originalBitmap.recycle()

                imageUri
            } catch (e: Exception) {
                uri // 실패 시 원본 반환
            }
        }
    }

    private fun getRotatedBitmap(uri: Uri, bitmap: Bitmap): Bitmap {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val exif = ExifInterface(inputStream!!)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            inputStream.close()

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270f)
                else -> bitmap
            }
        } catch (e: Exception) {
            Log.e("MyPageViewModel", "EXIF 회전 처리 실패: ${e.message}", e)
            bitmap
        }
    }

    // 비트맵을 회전시키는 함수
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

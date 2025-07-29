package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import com.google.firebase.storage.FirebaseStorage
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SavePetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetPetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.viewModelScope

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savePetProfileUseCase: SavePetProfileUseCase,
    private val getPetProfileUseCase: GetPetProfileUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {

    private val _saveProfileEvent = MutableSharedFlow<Boolean>()
    val saveProfileEvent: SharedFlow<Boolean> get() = _saveProfileEvent

    fun saveProfile(name: String, imageUrl: String?) {
        viewModelScope.launch {
            try {
                val profileImageUrl = if (imageUrl != null) {
                    try {
                        val currentProfile = getPetProfileUseCase().first()
                        currentProfile?.profileImageUrl?.let { oldUrl ->
                            if (oldUrl.isNotEmpty() && oldUrl.contains("firebase")) {
                                deleteOldProfileImage(oldUrl)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("ProfileViewModel", "기존 프로필 조회 실패: ${e.message}")
                    }

                    // 새 이미지 업로드 (userId 기반)
                    val userId = getUserIdUseCase.invoke()

                    uploadProfileImageToStorage(Uri.parse(imageUrl), userId.toString())
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

    private suspend fun uploadProfileImageToStorage(uri: Uri, userId: String): String {
        return try {
            // 이미지 최적화 (앨범 표지용)
            val optimizedImageUri = optimizeImageForAlbumCover(uri)

            val fileName = "profile_${userId}.jpg"
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
            // Firebase Storage URL에서 파일 경로 추출하여 삭제 (MyPageViewModel과 동일한 방식)
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
            storageRef.delete().await()
            Log.d("ProfileViewModel", "기존 프로필 이미지 삭제 완료")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "기존 프로필 이미지 삭제 실패: ${e.message}", e)
        }
    }

    private suspend fun optimizeImageForAlbumCover(uri: Uri): Uri {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val rotatedBitmap = getRotatedBitmap(uri, originalBitmap)

                val processedBitmap = createOptimalAlbumCoverBitmap(rotatedBitmap)

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
                    processedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                } ?: throw IOException("Failed to open output stream")

                // 메모리 정리
                if (processedBitmap != rotatedBitmap) {
                    processedBitmap.recycle()
                }
                if (rotatedBitmap != originalBitmap) {
                    rotatedBitmap.recycle()
                }
                originalBitmap.recycle()

                imageUri
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "이미지 최적화 실패: ${e.message}", e)
                uri // 실패 시 원본 반환
            }
        }
    }

    private fun createOptimalAlbumCoverBitmap(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 정사각형 크롭을 위한 크기 계산
        val size = min(width, height)
        val x = (width - size) / 2
        val y = (height - size) / 2

        // 중앙에서 정사각형으로 크롭
        val croppedBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)

        // 적응형 크기 조정: 원본 크기에 따라 목표 크기 결정
        val targetSize = when {
            size > 2048 -> 800  // 매우 큰 이미지는 800px로
            size > 1024 -> 600  // 큰 이미지는 600px로  
            size > 512 -> 512   // 중간 이미지는 512px로
            else -> size        // 작은 이미지는 원본 크기 유지
        }

        // 목표 크기로 리사이즈 (고품질 필터링)
        return if (size != targetSize) {
            val resizedBitmap =
                Bitmap.createScaledBitmap(croppedBitmap, targetSize, targetSize, true)
            if (croppedBitmap != bitmap) {
                croppedBitmap.recycle()
            }
            resizedBitmap
        } else {
            croppedBitmap
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
            Log.e("ProfileViewModel", "EXIF 회전 처리 실패: ${e.message}", e)
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

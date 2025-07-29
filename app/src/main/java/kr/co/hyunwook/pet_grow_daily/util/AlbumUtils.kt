package kr.co.hyunwook.pet_grow_daily.util

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
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.min

/**
 * 프로필 이미지 업로드/최적화/삭제 유틸리티 세트
 * ViewModel에서 context와 userId, Uri만 넘겨주면 바로 사용 가능.
 */

/**
 * 앨범 표지에 최적화된 이미지 비트맵 생성 및 Uri 반환 (용량/품질 균형, 정사각형 크롭)
 */
suspend fun optimizeImageForAlbumCover(context: Context, uri: Uri): Uri =
    withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val rotatedBitmap = getRotatedBitmap(context, uri, originalBitmap)
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

            if (processedBitmap != rotatedBitmap) {
                processedBitmap.recycle()
            }
            if (rotatedBitmap != originalBitmap) {
                rotatedBitmap.recycle()
            }
            originalBitmap.recycle()

            imageUri
        } catch (e: Exception) {
            Log.e("ProfileImageUtils", "이미지 최적화 실패: ${e.message}", e)
            uri // 실패 시 원본 반환
        }
    }

/**
 * 비트맵을 회전시켜주는 함수 (EXIF 기준)
 */
private fun getRotatedBitmap(context: Context, uri: Uri, bitmap: Bitmap): Bitmap {
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
        Log.e("ProfileImageUtils", "EXIF 회전 처리 실패: ${e.message}", e)
        bitmap
    }
}

private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

/**
 * 크롭 및 적응형 리사이즈(앨범 표지 최적화용)
 */
private fun createOptimalAlbumCoverBitmap(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val size = min(width, height)
    val x = (width - size) / 2
    val y = (height - size) / 2
    val croppedBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
    val targetSize = when {
        size > 2048 -> 800
        size > 1024 -> 600
        size > 512 -> 512
        else -> size
    }
    return if (size != targetSize) {
        val resizedBitmap = Bitmap.createScaledBitmap(croppedBitmap, targetSize, targetSize, true)
        if (croppedBitmap != bitmap) {
            croppedBitmap.recycle()
        }
        resizedBitmap
    } else {
        croppedBitmap
    }
}

/**
 * Firebase Storage에 최적화된 프로필 이미지를 업로드하고 다운로드 url 반환
 */
suspend fun uploadProfileImageToFirebase(context: Context, imageUri: Uri, userId: String): String {
    return try {
        val optimizedImageUri = optimizeImageForAlbumCover(context, imageUri)
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
        Log.e("ProfileImageUtils", "Firebase 업로드 실패: ${e.message}", e)
        throw e
    }
}

/**
 * 기존 firebase profile 이미지 삭제
 */
suspend fun deleteFirebaseProfileImage(oldImageUrl: String) {
    try {
        val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl)
        storageRef.delete().await()
        Log.d("ProfileImageUtils", "기존 프로필 이미지 삭제 완료")
    } catch (e: Exception) {
        Log.e("ProfileImageUtils", "기존 프로필 이미지 삭제 실패: ${e.message}", e)
    }
}

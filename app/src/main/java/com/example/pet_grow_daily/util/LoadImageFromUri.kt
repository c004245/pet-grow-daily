package com.example.pet_grow_daily.util

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
@SuppressLint("ObsoleteSdkInt")
@Composable
fun LoadImageFromUri(contentUri: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    LaunchedEffect(contentUri) {
        withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse(contentUri)
                val resolver = context.contentResolver

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    context.grantUriPermission(
                        context.packageName,
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }

                resolver.openInputStream(uri)?.use { stream ->
                    bitmapState.value = BitmapFactory.decodeStream(stream)
                }

            } catch (e: SecurityException) {
                Log.e("LoadImageFromUri", "SecurityException: ${e.message}")
            } catch (e: Exception) {
                Log.e("LoadImageFromUri", "Failed to load image", e)
            } finally {
                isLoading.value = false // 로딩 상태 종료
            }
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (isLoading.value) {
            CircularProgressIndicator() // 로딩 중 표시
        } else {
            bitmapState.value?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = modifier,
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Text(
                    text = "이미지를 로드할 수 없습니다.",
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

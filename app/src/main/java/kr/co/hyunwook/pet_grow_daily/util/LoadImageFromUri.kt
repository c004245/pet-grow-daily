package kr.co.hyunwook.pet_grow_daily.util

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LoadGalleryImage(
    uri: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current

    DisposableEffect(uri) {
        // 권한 확인 및 요청
        val persistedUris = context.contentResolver.persistedUriPermissions.map { it.uri }
        if (!persistedUris.contains(Uri.parse(uri))) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    Uri.parse(uri),
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                Log.e("LoadGalleryImage", "SecurityException while accessing URI: $uri", e)
            }
        }

        onDispose { }
    }

    // Glide로 이미지 로드
    GlideImage(
        model = uri,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
    )
}

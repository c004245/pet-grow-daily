package kr.co.hyunwook.pet_grow_daily.feature.albumimage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
fun AlbumImageRoute(
    navigateToAlbum: () -> Unit
) {
    LaunchedEffect(Unit) {

    }

    AlbumImageScreen(

    )
}

@Composable
fun AlbumImageScreen(
){
    Box(
        modifier = Modifier.fillMaxWidth()
    )
}

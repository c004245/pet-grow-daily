package kr.co.hyunwook.pet_grow_daily.feature.albumimage

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AlbumImageRoute(
    viewModel: AlbumViewModel = hiltViewModel()
) {

    val albumImageList by viewModel.albumImageList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumImageList()
    }

    AlbumImageScreen(
        albumImageList = albumImageList
    )
}

@Composable
fun AlbumImageScreen(
    albumImageList: List<AlbumImageModel>
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize().padding(top = 13.dp)
            .background(Color.Transparent),
        horizontalArrangement = Arrangement.spacedBy(1.dp),
        verticalArrangement = Arrangement.spacedBy(1.dp),
        contentPadding = PaddingValues(1.dp)
    ) {
        items(albumImageList) { albumImage ->
            AlbumImageGridItem(albumImage)
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumImageGridItem(albumImage: AlbumImageModel) {
    Box(
        modifier = Modifier.aspectRatio(1f)
            .fillMaxWidth()
    ) {
        GlideImage(
            model = albumImage.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

    }
}
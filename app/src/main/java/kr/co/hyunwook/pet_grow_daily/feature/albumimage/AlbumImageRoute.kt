package kr.co.hyunwook.pet_grow_daily.feature.albumimage

import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumViewModel
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation.AlbumImage
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AlbumImageRoute(
    navigateToAlbum: () -> Unit,
    viewModel: AlbumViewModel = hiltViewModel()
) {

    val albumImageList by viewModel.albumImageList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumImageList()
    }

    AlbumImageScreen(
        albumImageList = albumImageList,
        navigateToAlbum = navigateToAlbum
    )
}

@Composable
fun AlbumImageScreen(
    albumImageList: List<AlbumImageModel>,
    navigateToAlbum: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CommonTopBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.text_album_title),
                        style = PetgrowTheme.typography.bold,
                        color = black21
                    )
                },
                icon = {
                    IconButton(onClick = {
                        navigateToAlbum()
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_album_home),
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}

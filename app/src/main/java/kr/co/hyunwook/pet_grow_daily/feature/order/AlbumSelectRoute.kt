package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayEF
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.formatDate

@Composable
fun AlbumSelectRoute(
    viewModel: OrderViewModel = hiltViewModel()

) {
    val albumRecord by viewModel.albumRecord.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumSelectRecord()
    }

    AlbumSelectScreen(
        albumRecord = albumRecord
    )
}

@Composable
fun AlbumSelectScreen(
    albumRecord: List<AlbumRecord>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleAppBar()
        AlbumListSelectWidget(
            modifier = Modifier.padding(top = 16.dp),
            albumRecordItem = albumRecord
        )
    }
}

@Composable
fun TitleAppBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "ic_back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable {

                }
        )
        Text(
            text = stringResource(R.string.text_picture_select),
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun AlbumListSelectWidget(
    modifier: Modifier,
    albumRecordItem: List<AlbumRecord>
) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                top = 16.dp,
                bottom = 80.dp
            ),
        ) {
            itemsIndexed(albumRecordItem) { index, item ->
                AlbumSelectCard(
                    albumRecord = item,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                .clickable {  }
                .clip(RoundedCornerShape(14.dp))
                .background(purple6C)
        ) {

            Text(
                text = "사진 선택 (0/40)",
                color = Color.White,
                fontSize = 14.sp,
                style = PetgrowTheme.typography.medium,
                modifier = Modifier.padding(top = 14.dp, bottom = 14.dp).align(Alignment.Center)
            )
        }
    }
}

@Composable
fun AlbumSelectCard(
    albumRecord: AlbumRecord,
    modifier: Modifier
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0x0D000000),
                    ambientColor = Color(0x0D000000)
                )
                .clip(RoundedCornerShape(16.dp)) // 전체 카드에 클립 적용
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.6f)
                            )
                        ),
                    )
                    .blur(radius = 3.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                AlbumImageSelectWidget(
                    firstImageUri = albumRecord.firstImage,
                    secondImageUri = albumRecord.secondImage,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                AlbumTextSelect(
                    date = albumRecord.date,
                    content = albumRecord.content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                )
            }
        }
    }
}


//앨범 이미지 영역
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumImageSelectWidget(
    firstImageUri: String,
    secondImageUri: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .background(grayEF, RoundedCornerShape(topStart = 16.dp))
        ) {
            GlideImage(
                model = firstImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = placeholder(ColorPainter(grayEF)),
                failure = placeholder(ColorPainter(grayEF))
            )
        }
        Spacer(modifier = Modifier.width(2.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .background(grayEF, RoundedCornerShape(topEnd = 16.dp))
        ) {
            GlideImage(
                model = secondImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = placeholder(ColorPainter(grayEF)),
                failure = placeholder(ColorPainter(grayEF))
            )
        }
    }
}

//앨범 텍스트 영역
@Composable
fun AlbumTextSelect(
    date: Long,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = formatDate(date),
            style = PetgrowTheme.typography.medium,
            fontSize = 14.sp,
            color = black21,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            color = gray5E,
            fontSize = 14.sp,
            style = PetgrowTheme.typography.regular,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

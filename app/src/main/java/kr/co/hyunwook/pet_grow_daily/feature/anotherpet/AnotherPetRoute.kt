package kr.co.hyunwook.pet_grow_daily.feature.anotherpet

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import com.google.accompanist.pager.rememberPagerState
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayEF
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnotherPetRoute(
    viewModel: AnotherPetViewModel = hiltViewModel()
) {

    val anotherImageList by viewModel.anotherPetList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAnotherPetList()
    }
    AnotherPetScreen(
        anotherImageList
    )
}

@Composable
fun AnotherPetScreen(
    anotherImageList: List<AnotherPetModel>
) {

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                CommonTopBar(
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.text_take_a_look),
                            style = PetgrowTheme.typography.bold,
                            color = black21
                        )
                    }
                )
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(anotherImageList) { index, item ->
                LookAnotherItem(item)
                if (index < anotherImageList.lastIndex) {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LookAnotherItem(item: AnotherPetModel) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val imageList = remember {
            listOf(item.firstImage, item.secondImage)
        }

        var pagerState = androidx.compose.foundation.pager.rememberPagerState(
            initialPage = 0,
            pageCount = { 2 })

        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(1f)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                GlideImage(
                    model = imageList[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = placeholder(ColorPainter(grayEF)),
                    failure = placeholder(ColorPainter(grayEF))
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Row(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (i in imageList.indices) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    if (pagerState.currentPage == i) Color.White
                                    else Color.White.copy(alpha = 0.5f)
                                )
                        )
                    }
                }
            }
        }
        Text(
            text = item.content,
            style = PetgrowTheme.typography.regular,
            color = gray5E,
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp)
        )

    }

}


package kr.co.hyunwook.pet_grow_daily.feature.anotherpet

import android.util.Log
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
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
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMoreData by viewModel.hasMoreData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAnotherPetList()
    }

    AnotherPetScreen(
        anotherImageList = anotherImageList,
        isLoading = isLoading,
        hasMoreData = hasMoreData,
        onLoadMore = {
            viewModel.loadMoreData()
        }
    )
}

@Composable
fun AnotherPetScreen(
    anotherImageList: List<AnotherPetModel>,
    isLoading: Boolean,
    hasMoreData: Boolean,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()


    val shouldLoadMore by remember(anotherImageList.size) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()

            // 리스트가 비어있으면 스크롤 로드 안 함 (첫 로드가 완료되지 않았음)
            if (anotherImageList.isEmpty()) {
                return@derivedStateOf false
            }

            val shouldLoad =
                lastVisibleItem != null && lastVisibleItem.index >= anotherImageList.size - 3


            shouldLoad
        }
    }

    // 끝에 도달하면 더 많은 데이터 로드
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasMoreData && !isLoading) {
            onLoadMore()
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(
            state = listState,
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


            if (isLoading && anotherImageList.isNotEmpty()) {
                item {
                    LoadingIndicator()
                }
            }

            // 더 이상 데이터가 없을 때 표시
            if (!hasMoreData && anotherImageList.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.text_another_pet_done),
                        style = PetgrowTheme.typography.medium,
                        color = gray5E,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingIndicator() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.loading_animation)
        )

        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.text_another_more_image),
            style = PetgrowTheme.typography.medium,
            color = gray5E,
            fontSize = 14.sp
        )
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
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .shadow(8.dp, RoundedCornerShape(12.dp), clip = false)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    GlideImage(
                        model = imageList[page],
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = placeholder(ColorPainter(grayEF)),
                        failure = placeholder(ColorPainter(grayEF))
                    )
                }
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
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        )

    }

}


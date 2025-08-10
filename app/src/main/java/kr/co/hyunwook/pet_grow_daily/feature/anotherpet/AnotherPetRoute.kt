package kr.co.hyunwook.pet_grow_daily.feature.anotherpet

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.whiteF8

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
        },
        onRefresh = {
            viewModel.getAnotherPetList()
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AnotherPetScreen(
    anotherImageList: List<AnotherPetModel>,
    isLoading: Boolean,
    hasMoreData: Boolean,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit
) {
    val listState = rememberLazyListState()
    val pullRefreshState = rememberPullRefreshState(refreshing = isLoading, onRefresh = onRefresh)

    val shouldLoadMore by remember(anotherImageList.size) {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()

            if (anotherImageList.isEmpty()) {
                return@derivedStateOf false
            }

            val shouldLoad =
                lastVisibleItem != null && lastVisibleItem.index >= anotherImageList.size - 3


            shouldLoad
        }
    }
    
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && hasMoreData && !isLoading) {
            onLoadMore()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(whiteF8)
            .pullRefresh(pullRefreshState)
    ) {
        if (isLoading) {
            PullRefreshIndicator(
                refreshing = true,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
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

            }


            if (isLoading && anotherImageList.isNotEmpty()) {
                item {
                    LoadingIndicator()
                }
            }

            // 더 이상 데이터가 없을 때 표시
            if (!hasMoreData && anotherImageList.isNotEmpty()) {
                item {
                   AnotherLastWidget()
                }
            }
        }
    }
}

@Composable
fun AnotherLastWidget() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.ic_another_around_done),
                contentDescription = null,
                modifier = Modifier.padding(top = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.text_another_pet_done),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 20.dp)
            )
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .shadow(
                elevation = 8.dp,
                spotColor = Color(0x1A000000),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            val imageList = remember {
                listOf(item.firstImage, item.secondImage)
            }

            var pagerState = androidx.compose.foundation.pager.rememberPagerState(
                initialPage = 0,
                pageCount = { 2 })

            // 이미지 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
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

                // 페이지 인디케이터
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
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

            // 텍스트 영역
            Text(
                text = item.content,
                style = PetgrowTheme.typography.regular,
                color = gray5E,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }
    }
}

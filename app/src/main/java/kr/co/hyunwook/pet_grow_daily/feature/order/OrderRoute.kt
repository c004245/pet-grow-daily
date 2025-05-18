package kr.co.hyunwook.pet_grow_daily.feature.order

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.redF3
import kr.co.hyunwook.pet_grow_daily.feature.add.TitleAppBar
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.copy

@Composable
fun OrderRoute(
    viewModel: OrderViewModel = hiltViewModel()
) {

    val userAlbumCount by viewModel.userAlbumCount.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getUserAlbumCount()

    }

    OrderScreen()
}

@Composable
fun OrderScreen() {
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
                CommonTopBar(
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.text_order_title),
                            style = PetgrowTheme.typography.bold,
                            color = black21
                        )
                    }
                )

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(13.dp))
                ImageSliderWithIndicator()
                Spacer(Modifier.height(24.dp))
                ProductDescriptionWidget()
                Spacer(Modifier.height(16.dp))
                OrderCountWidget()
            }
        }
            OrderButtonWidget(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            )
        }



}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSliderWithIndicator() {
    val images = remember {
        listOf(
            R.drawable.ic_dummy1,
            R.drawable.ic_dummy2,
            R.drawable.ic_dummy1,
            R.drawable.ic_dummy2,
        )
    }


    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    LaunchedEffect(key1 = pagerState) {
        while (true) {
            delay (5000)
            val nextPage = if (pagerState.currentPage + 1< images.size) {
                pagerState.currentPage + 1
            } else {
                0
            }
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(300.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Slider Image $page",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            Modifier.align(Alignment.BottomCenter).padding(8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Log.d("HWO", "images -> ${images.size}")
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    Color.White
                else
                    gray86


                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color = color)
                        .size(8.dp)
                )
            }

        }
    }
}


//추후에 Firebase remote config 로 가격 조정
@Composable
fun ProductDescriptionWidget() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)

    ) {
        Text(
            text = "코팅형 고급 앨범 (사진 최대 40장)",
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "30,000원",
            style = PetgrowTheme.typography.medium,
            textDecoration = TextDecoration.LineThrough,
            color = gray86,
            fontSize = 13.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "10%",
                style = PetgrowTheme.typography.bold,
                color = redF3,
                fontSize = 22.sp
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = "27,000원",
                color = black21,
                fontSize = 22.sp,
                style = PetgrowTheme.typography.bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "배송비 3,000원",
            style = PetgrowTheme.typography.medium,
            color = gray86,
            fontSize = 14.sp
        )
    }

}

@Composable
fun OrderCountWidget() {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(grayf1, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "오늘 주문 가능 수량",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = black21
            )
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "10개",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = purple6C
            )
        }

    }
}

@Composable
fun OrderButtonWidget(modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.background(purple6C, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp),
                text = "사진을 6개 더 채우면 주문 가능해요.",
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )
        }
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_down_order),
            contentDescription = null
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier.fillMaxWidth().background(purple6C, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(vertical = 14.dp),
                text = "앨범 주문",
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }

}
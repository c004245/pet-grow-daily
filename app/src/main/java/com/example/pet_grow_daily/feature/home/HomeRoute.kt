package com.example.pet_grow_daily.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayDE
import com.example.pet_grow_daily.core.designsystem.theme.grayF8
import com.example.pet_grow_daily.core.designsystem.theme.purpleE6
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import kotlin.math.absoluteValue

@Composable
fun HomeRoute(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    HomeScreen(
        paddingValues = paddingValues
    )
}

@Composable
fun HomeScreen(
    paddingValues: PaddingValues
) {

    val imageList = listOf(
        R.drawable.ic_background_dummy,
        R.drawable.ic_background_dummy,
        R.drawable.ic_background_dummy,
        R.drawable.ic_background_dummy,
        R.drawable.ic_background_dummy,
    )
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        CommonTopBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.text_today_grow_title),
                    style = PetgrowTheme.typography.bold
                )

            }
        )
        CustomTodayGrowViewPager(
            modifier = Modifier.padding(top = 52.dp),
            items = imageList
        )
//        EmptyTodayGrowRecordWidget(
//            modifier = Modifier.padding(40.dp),
//            isFullHeight = true
//        ) {
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.Center
//            ) {
//                Text(
//                    text = stringResource(id = R.string.text_not_today_grow),
//                    style = PetgrowTheme.typography.medium,
//                    color = gray86
//
//                )
//            }
//        }

    }
}

@Composable
fun EmptyTodayGrowRecordWidget(
    modifier: Modifier = Modifier,
    isFullHeight: Boolean = false,
    borderColor: Color = grayDE,
    borderWidth: Dp = 2.dp,
    cornerRadius: Dp = 16.dp,
    content: @Composable () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isFullHeight) {
                    Modifier.fillMaxHeight()
                } else {
                    Modifier.aspectRatio(1f) // Set height equal to screenWidthDp for 1:1 aspect ratio
                }
            )
            .background(Color.White)
            .drawBehind {
                val strokeWidth = borderWidth.toPx()
                val dashWidth = 10f
                val dashGap = 10f
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
                drawRoundRect(
                    color = borderColor,
                    style = Stroke(width = strokeWidth, pathEffect = pathEffect),
                    cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}


@Composable
fun CustomTodayGrowViewPager(modifier: Modifier, items: List<Int>) {

    val listState = rememberLazyListState()

    val currentPage by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                visibleItems.minByOrNull {
                    (it.offset + it.size / 2 - layoutInfo.viewportEndOffset / 2).absoluteValue
                }?.index ?: 0
            } else 0
        }
    }
    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 40.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        itemsIndexed(items) { index, item ->
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = 1f - (currentPage - index).absoluteValue * 0.15f
                        scaleX = scale
                        scaleY = scale
                    }
                    .padding(16.dp)
                    .fillParentMaxWidth(0.8f)
                    .wrapContentHeight()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(8.dp),
                        clip = false
                    )
                    .background(Color.White, RoundedCornerShape(8.dp))

            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = item), // 이미지 리소스
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // 1:1 비율
                            .clip(RoundedCornerShape(12.dp)) //
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TodayCardDescription()


                }
            }
        }
    }
}

@Composable
fun TodayCardDescription() {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp).wrapContentHeight(),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "산책", // 제목 텍스트
                color = Color.Black,
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(purpleE6),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_hospital_select),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            text = "2022.06.01(일) 14:56",
            color = gray86,
            fontSize = 12.sp,
            style = PetgrowTheme.typography.medium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                .background(grayF8)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = "테스트테스트테스트테스트",
                color = black21,
                fontSize = 12.sp,
                style = PetgrowTheme.typography.regular
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}
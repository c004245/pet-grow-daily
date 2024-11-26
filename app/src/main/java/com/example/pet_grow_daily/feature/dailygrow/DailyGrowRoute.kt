package com.example.pet_grow_daily.feature.dailygrow

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.bumptech.glide.integration.compose.GlideImage
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayDE
import com.example.pet_grow_daily.core.designsystem.theme.grayF8
import com.example.pet_grow_daily.core.designsystem.theme.purpleD9
import com.example.pet_grow_daily.feature.main.SelectTab
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import com.example.pet_grow_daily.util.LoadGalleryImage
import com.example.pet_grow_daily.util.formatTimestampToDateTime
import com.example.pet_grow_daily.util.getCategoryItem
import com.example.pet_grow_daily.util.getCategoryType
import com.example.pet_grow_daily.util.getMemoOrRandomQuote
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun DailyGrowRoute(
    paddingValues: PaddingValues,
    viewModel: DailyGrowViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val todayGrowRecords by viewModel.todayGrowRecords.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getGrowRecord(getTodayDate())
    }
    DailyGrowScreen(
        paddingValues = paddingValues,
        todayGrowRecords = todayGrowRecords
    )
}

@Composable
fun DailyGrowScreen(
    paddingValues: PaddingValues,
    todayGrowRecords: List<GrowRecord>
) {

    val coroutineScope = rememberCoroutineScope()
    
    val infiniteTransition = rememberInfiniteTransition()
    val tooltipOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = ""
    )
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
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

            if (todayGrowRecords.isNotEmpty()) {
                CustomTodayGrowViewPager(
                    modifier = Modifier.padding(top = 52.dp),
                    growRecordItem = todayGrowRecords
                )
            } else {
                EmptyTodayGrowRecordWidget(
                    modifier = Modifier
                        .padding(40.dp)
                        .fillMaxSize(),
                    isFullHeight = true
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.text_not_today_grow),
                            style = PetgrowTheme.typography.medium,
                            color = gray86,
                            fontSize = 12.sp

                        )
                    }
                }
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_dailygrow_tooltip),
            contentDescription = "tooltip",
            modifier = Modifier
                .align(Alignment.BottomCenter) // 하단 중앙 정렬
                .padding(bottom = 16.dp + tooltipOffset.dp) // 애니메이션 오프셋 추가
        )
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
fun CustomTodayGrowViewPager(modifier: Modifier, growRecordItem: List<GrowRecord>) {

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

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val itemWidth = screenWidth - (2 * 40.dp) - 16.dp

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(
            start = (screenWidth - itemWidth) / 2,
            end = (screenWidth - itemWidth) / 2
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        itemsIndexed(growRecordItem) { index, item ->
            Log.d("HWO", "DailyGrowROute -> ${item.photoUrl}")
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        val scale = 1f - (currentPage - index).absoluteValue * 0.15f
                        scaleX = scale
                        scaleY = scale
                    }
                    .width(itemWidth) // 아이템의 너비
                    .wrapContentHeight()
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(16.dp),
                        clip = true
                    )
                    .background(Color.White, RoundedCornerShape(8.dp))

            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    LoadGalleryImage(uri = item.photoUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(16.dp)))

                    Spacer(modifier = Modifier.height(16.dp))
                    TodayCardDescription(item)

                }
            }
        }
    }
}

@Composable
fun TodayCardDescription(growRecord: GrowRecord) {
    Log.d("HWO", "growRecord -> ${growRecord.categoryType} -- ${growRecord.emotionType} -- ${growRecord.memo} -- ${growRecord.timeStamp}")
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getCategoryType(growRecord.categoryType), // 제목 텍스트
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
                    .background(purpleD9),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = getCategoryItem(categoryType = growRecord.categoryType, SelectTab.DAILYGROW),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            text = formatTimestampToDateTime(growRecord.timeStamp),
            color = gray86,
            fontSize = 12.sp,
            style = PetgrowTheme.typography.medium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(grayF8)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = getMemoOrRandomQuote(growRecord.memo),
                color = black21,
                fontSize = 12.sp,
                style = PetgrowTheme.typography.regular
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}

fun getTodayDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}
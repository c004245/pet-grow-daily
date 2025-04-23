package kr.co.hyunwook.pet_grow_daily.feature.album

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleD9
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.Album
import kr.co.hyunwook.pet_grow_daily.feature.main.SelectTab
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.LoadGalleryImage
import kr.co.hyunwook.pet_grow_daily.util.formatTimestampToDateTime
import kr.co.hyunwook.pet_grow_daily.util.getCategoryItem
import kr.co.hyunwook.pet_grow_daily.util.getCategoryType
import kr.co.hyunwook.pet_grow_daily.util.getEmotionItem
import kr.co.hyunwook.pet_grow_daily.util.getMemoOrRandomQuote
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.absoluteValue
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.text.style.TextAlign

@Composable
fun AlbumRoute(
    paddingValues: PaddingValues,
    navigateToAdd: () -> Unit = {},
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val albumRecord by viewModel.albumRecord.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumRecord()
    }
    AlbumScreen(
        albumRecord = albumRecord,
        navigateToAdd = navigateToAdd
    )
}

@Composable
fun AlbumScreen(
    albumRecord: List<AlbumRecord>,
    navigateToAdd: () -> Unit = {}
) {

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
                        text = stringResource(id = R.string.text_album_title),
                        style = PetgrowTheme.typography.bold
                    )

                }
            )

            if (albumRecord.isNotEmpty()) {
                CustomTodayGrowViewPager(
                    modifier = Modifier.padding(top = 52.dp),
                    albumRecordItem = albumRecord
                )
            } else {
                EmptyAlbumWidget(
                    navigateToAdd = navigateToAdd
                )
            }
        }
    }
}


@Composable
fun CustomTodayGrowViewPager(modifier: Modifier, albumRecordItem: List<AlbumRecord>) {

    val listState = rememberLazyListState()

    val currentPage by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            visibleItems.minByOrNull {
                (it.offset + it.size / 2 - layoutInfo.viewportEndOffset / 2).absoluteValue
            }?.index ?: 0
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
        itemsIndexed(albumRecordItem) { index, item ->
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
//                    LoadGalleryImage(
//                        uri = item.photoUrl,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .aspectRatio(1f)
//                            .clip(RoundedCornerShape(16.dp))
//                    )
//
//                    Spacer(modifier = Modifier.height(16.dp))
//                    TodayCardDescription(item)

                }
            }
        }
    }
}

@Composable
fun EmptyAlbumWidget(
    navigateToAdd: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.text_no_album),
                color = gray86,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                style = PetgrowTheme.typography.medium,
                modifier = Modifier.wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.wrapContentWidth()
                    .clickable {
                        navigateToAdd()
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(purple6C)
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_plus),
                        modifier = Modifier.padding(start = 24.dp),
                        contentDescription = "add album"
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text(
                        text = stringResource(R.string.text_picture_add),
                        color = Color.White,
                        fontSize = 14.sp,
                        style = PetgrowTheme.typography.medium,
                        modifier = Modifier.padding(top = 14.dp, bottom = 14.dp, end = 24.dp)
                    )

                }

            }

        }
    }
}

@Composable
fun TodayCardDescription(growRecord: GrowRecord) {
    Log.d(
        "HWO",
        "growRecord -> ${growRecord.categoryType} -- ${growRecord.emotionType} -- ${growRecord.memo} -- ${growRecord.timeStamp}"
    )
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
                    painter = getCategoryItem(
                        categoryType = growRecord.categoryType,
                        SelectTab.ALBUM
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(purpleD9),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = getEmotionItem(emotionType = growRecord.emotionType),
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


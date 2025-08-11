package kr.co.hyunwook.pet_grow_daily.feature.album

import android.util.Log
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.ALBUM_CREATE_COMPLETE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayEF
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.AlbumImageRoute
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.formatDate
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kr.co.hyunwook.pet_grow_daily.util.MAX_ALBUM_COUNT

@Composable
fun AlbumRoute(
    paddingValues: PaddingValues,
    navigateToAdd: () -> Unit = {},
    navigateToAnotherPet: () -> Unit,
    navigateToOrderProductList: () -> Unit = {},
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val albumRecord by viewModel.albumRecord.collectAsState()
    val todayUserCount by viewModel.todayUserPhotoCount.collectAsState()
    val isDisableUpload by viewModel.isDisableUpload.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getShouldDisableUpload()
    }
    LaunchedEffect(Unit) {
        viewModel.getAlbumRecord()
    }

    LaunchedEffect(Unit) {
        viewModel.getTodayUserPhotoCount()
    }

    AlbumScreen(
        albumRecord = albumRecord,
        isDisableUpload = isDisableUpload,
        todayUserCount = todayUserCount,
        isLoading = isLoading,
        navigateToAdd = navigateToAdd,
        navigateToAnotherPet = navigateToAnotherPet,
        navigateToOrderProductList  = navigateToOrderProductList
    )
}

@Composable
fun AlbumScreen(
    albumRecord: List<AlbumRecord>,
    isDisableUpload: Boolean,
    todayUserCount: Int,
    isLoading: Boolean,
    navigateToAdd: () -> Unit = {},
    navigateToAnotherPet: () -> Unit = {},
    navigateToOrderProductList: () -> Unit
) {

    var selectedTab by remember { mutableStateOf(AlbumTab.LIST) }

    var pagerState = rememberPagerState(initialPage = 0, pageCount = { 2 })

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(
            page = if (selectedTab == AlbumTab.LIST) 0 else 1
        )
    }

    LaunchedEffect(pagerState.currentPage) {
        selectedTab = if (pagerState.currentPage == 0) AlbumTab.LIST else AlbumTab.GRID
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(grayF8)
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize()
            )
//            val lottieComposition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loading_animation))
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White.copy(alpha = 0.9f)),
//                contentAlignment = Alignment.Center
//            ) {
//                Column(
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    LottieAnimation(
//                        composition = lottieComposition,
//                        iterations = LottieConstants.IterateForever,
//                        modifier = Modifier
//                            .height(200.dp)
//                            .fillMaxWidth()
//                            .padding(horizontal = 36.dp)
//                    )
//                    androidx.compose.material.Text(
//                        text = "저장한 앨범 정보를 가져오고 있어요...",
//                        style = PetgrowTheme.typography.medium,
//                        color = black21,
//                        fontSize = 16.sp
//                    )
//                }
//            }
        } else {
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
                        AlbumSwitch(
                            selectedTab = selectedTab,
                            onTablSelected = {
                                selectedTab = it
                            }
                        )
                    }
                )

                AlbumTodayUploadWidget(todayUserCount, navigateToAnotherPet)
                if (pagerState.currentPage == 0) {
                    Spacer(Modifier.height(16.dp))
                    AlbumProgressWidget(
                        progress = albumRecord.size,
                        navigateToOrderProductList = navigateToOrderProductList
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> {
                            if (albumRecord.isNotEmpty()) {
                                Log.d("HWO", "AlbumCard show")
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                                ) {
                                    itemsIndexed(albumRecord) { index, item ->
                                        AlbumCard(
                                            albumRecord = item,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp)
                                                .padding(bottom = 16.dp)
                                        )
                                    }
                                }
                            } else {
                                Log.d("HWO", "AlbumCard show 2")
                                EmptyAlbumWidget(
                                    navigateToAdd = navigateToAdd,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        1 -> {
                            AlbumImageRoute(
                                navigateToAdd = navigateToAdd
                            )
                        }
                    }
                }
            }
        }

        if (albumRecord.isNotEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clickable {
                        //결제 테스트를 위한 주석 필요
                        if (!isDisableUpload) {
                            navigateToAdd()
                        }
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDisableUpload) purple6C.copy(alpha = 0.4f) else purple6C)

            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isDisableUpload) {
                        Text(
                            text = stringResource(R.string.text_today_album_done),
                            color = Color.White,
                            fontSize = 14.sp,
                            style = PetgrowTheme.typography.medium,
                            modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.ic_plus),
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
                            modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlbumTodayUploadWidget(todayCount: Int, navigateToAnotherPet: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(black21)
            .clickable {
                navigateToAnotherPet()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "오늘 ${todayCount}명이 사진을 공유했어요.",
                style = PetgrowTheme.typography.medium,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
            )

            Image(
                painter = painterResource(R.drawable.ic_today_upload_arrow),
                contentDescription = "arrow",
                modifier = Modifier.padding(end = 20.dp)
            )
        }
    }
    
}


@Composable
fun AlbumCard(
    albumRecord: AlbumRecord,
    modifier: Modifier
) {
    Box(
        modifier = modifier
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
                AlbumImageWidget(
                    firstImageUri = albumRecord.firstImage,
                    secondImageUri = albumRecord.secondImage,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                AlbumText(
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
fun AlbumImageWidget(
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
fun AlbumText(
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

@Composable
fun EmptyAlbumWidget(
    navigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()

    ) {
        // Background image at the bottom - positioned absolutely to avoid padding
        Image(
            painter = painterResource(R.drawable.ic_empty_background),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )

        // Content centered
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
                    modifier = Modifier
                        .wrapContentWidth()
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
}

@Composable
fun AlbumSwitch(
    selectedTab: AlbumTab,
    onTablSelected: (AlbumTab) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(
                RoundedCornerShape(8.dp)
            )
            .background(grayEF)
            .padding(3.dp)
    ) {
        ToggleTabItem(
            tab = AlbumTab.LIST,
            isSelected = selectedTab == AlbumTab.LIST,
            onClick = onTablSelected,
            selectedIconRes = R.drawable.ic_album_select,
            unSelectedIconRes = R.drawable.ic_album_unselect
        )
        ToggleTabItem(
            tab = AlbumTab.GRID,
            isSelected = selectedTab == AlbumTab.GRID,
            onClick = onTablSelected,
            selectedIconRes = R.drawable.ic_my_album_select,
            unSelectedIconRes = R.drawable.ic_my_album_unselect
        )
    }
}

@Composable
fun ToggleTabItem(
    tab: AlbumTab,
    isSelected: Boolean,
    onClick: (AlbumTab) -> Unit,
    selectedIconRes: Int,
    unSelectedIconRes: Int
) {
    Box(
        modifier = Modifier
            .clip(
                RoundedCornerShape(6.dp)
            )
            .background(if (isSelected) Color.White else Color.Transparent)
            .clickable { onClick(tab) }
            .padding(6.dp)
    ) {
        Image(
            painter = painterResource(id = if (isSelected) selectedIconRes else unSelectedIconRes),
            contentDescription = tab.name,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun AlbumProgressWidget(
    progress: Int,
    navigateToOrderProductList: (() -> Unit)? = null
) {
    val currentCount = progress * 2
    val maxCount = ALBUM_CREATE_COMPLETE
    val progressRatio = currentCount.toFloat() / maxCount

    val isCompleted = currentCount >= maxCount

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable {
                if (isCompleted && navigateToOrderProductList != null) {
                    navigateToOrderProductList()
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isCompleted) {
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.text_add_picture_completed), // 또는 적절한 문자열
                            style = PetgrowTheme.typography.medium, // 스타일 변경 가능
                            color = purple6C,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.width(4.dp))
                        if (navigateToOrderProductList != null) {
                            Image(
                                painter = painterResource(R.drawable.ic_progress_arrow),
                                contentDescription = null
                            )
                        }
                    }
                } else {
                    // 기존 텍스트 표시
                    Text(
                        text = "${MAX_ALBUM_COUNT - currentCount}장만 더 채우면 앨범을 주문할 수 있어요.",
                        style = PetgrowTheme.typography.medium,
                        color = purple6C,
                        fontSize = 12.sp,
                    )
                }
                Text(
                    text = "$currentCount/$ALBUM_CREATE_COMPLETE",
                    style = PetgrowTheme.typography.medium,
                    color = purple6C,
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(purple6C.copy(alpha = 0.2f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressRatio) // 0.0f에서 0.2f로 변경 (8/40 = 0.2)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(purple6C)
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

enum class AlbumTab {
    LIST, GRID
}

package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayEF
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

enum class AlbumLayoutType {
    LAYOUT_A, // 세로형 2개
    LAYOUT_B  // 가로형 2개
}

@Composable
fun AlbumLayoutRoute(
    viewModel: OrderViewModel,
    navigateToDeliveryRegister: () -> Unit,
    navigateToDeliveryCheck: () -> Unit,
    onBackClick: () -> Unit = {}
) {
    BackHandler {
        onBackClick()
    }

    AlbumLayoutScreen(
        onBackClick = onBackClick
    )
}

@Composable
fun AlbumLayoutScreen(
    onBackClick: () -> Unit
) {
    var selectedLayout by remember { mutableStateOf<AlbumLayoutType?>(null) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TitleAppBar {
            onBackClick()
        }
        Spacer(Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.text_album_layout_title),
            style = PetgrowTheme.typography.bold,
            color = gray5E,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(32.dp))

        // 첫 번째 레이아웃 (세로형)
        AlbumLayoutItem(
            layoutType = AlbumLayoutType.LAYOUT_A,
            isSelected = selectedLayout == AlbumLayoutType.LAYOUT_A,
            onSelected = { selectedLayout = AlbumLayoutType.LAYOUT_A },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(20.dp))

        // 두 번째 레이아웃 (가로형)
        AlbumLayoutItem(
            layoutType = AlbumLayoutType.LAYOUT_B,
            isSelected = selectedLayout == AlbumLayoutType.LAYOUT_B,
            onSelected = { selectedLayout = AlbumLayoutType.LAYOUT_B },
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun AlbumLayoutItem(
    layoutType: AlbumLayoutType,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelected() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) purple6C else grayDE,
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // 체크박스
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) purple6C else Color.Transparent
                    )
                    .border(
                        width = 2.dp,
                        color = if (isSelected) purple6C else grayEF,
                        shape = CircleShape
                    )
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // 레이아웃 미리보기
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (layoutType) {
                    AlbumLayoutType.LAYOUT_A -> {
                        // 세로형 레이아웃
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(120.dp)
                                    .background(
                                        color = grayEF,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(100.dp)
                                    .background(
                                        color = grayEF,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }

                    AlbumLayoutType.LAYOUT_B -> {
                        // 가로형 레이아웃
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(160.dp)
                                    .height(60.dp)
                                    .background(
                                        color = grayEF,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .let {
                                        if (isSelected && layoutType == AlbumLayoutType.LAYOUT_B) {
                                            it.border(
                                                width = 2.dp,
                                                color = purple6C,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        } else it
                                    }
                            ) {
                                if (isSelected && layoutType == AlbumLayoutType.LAYOUT_B) {
                                    // 십자가 표시 (가로형 선택 시)
                                    Box(
                                        modifier = Modifier.align(Alignment.Center)
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.ic_plus),
                                            contentDescription = null,
                                            tint = purple6C,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // 크기 표시
                                    Text(
                                        text = "90 x 120",
                                        fontSize = 10.sp,
                                        color = Color.White,
                                        modifier = Modifier
                                            .background(
                                                color = purple6C,
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .align(Alignment.BottomStart)
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(80.dp)
                                    .background(
                                        color = grayEF,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "텍스트는 여기에 표시됩니다.",
                    style = PetgrowTheme.typography.medium,
                    color = gray5E,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
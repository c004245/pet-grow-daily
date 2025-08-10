package kr.co.hyunwook.pet_grow_daily.feature.order

import TitleDeliveryAppBarOnlyButton
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayEF
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumLayout
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

enum class AlbumLayoutType {
    LAYOUT_A, //
    LAYOUT_B  //
}

@Composable
fun AlbumLayoutRoute(
    viewModel: OrderViewModel,
    navigateToDeliveryRegister: () -> Unit,
    navigateToDeliveryCheck: () -> Unit,
    onBackClick: () -> Unit = {}
) {

    val hasDeliveryInfo by viewModel.hasDeliveryInfo.collectAsState()
    var shouldNavigate by remember { mutableStateOf(false) }
    BackHandler {
        onBackClick()
    }

    LaunchedEffect(hasDeliveryInfo, shouldNavigate) {
        if (shouldNavigate) {
            delay(100)
            if (hasDeliveryInfo) {
                navigateToDeliveryCheck()
            } else {
                navigateToDeliveryRegister()
            }
            shouldNavigate = false
        }
    }

    AlbumLayoutScreen(
        navigateToDeliveryInfo = { selectedLayout ->
            viewModel.setSelectedAlbumLayout(selectedLayout)
            viewModel.checkDeliveryInfo()
            shouldNavigate = true
        },
        onBackClick = onBackClick
    )
}

@Composable
fun AlbumLayoutScreen(
    navigateToDeliveryInfo: (AlbumLayoutType) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedLayout by remember { mutableStateOf<AlbumLayoutType?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .padding(bottom = 80.dp)
        ) {
            TitleDeliveryAppBarOnlyButton {
                onBackClick()
            }
            Spacer(Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.text_album_layout_title),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 24.sp,
            )

            Spacer(Modifier.height(40.dp))

            // 첫 번째 레이아웃 (세로형)
            AlbumLayoutItem(
                layoutType = AlbumLayoutType.LAYOUT_A,
                isSelected = selectedLayout == AlbumLayoutType.LAYOUT_A,
                onSelected = { selectedLayout = AlbumLayoutType.LAYOUT_A },
            )

            Spacer(Modifier.height(20.dp))

            // 두 번째 레이아웃 (가로형)
            AlbumLayoutItem(
                layoutType = AlbumLayoutType.LAYOUT_B,
                isSelected = selectedLayout == AlbumLayoutType.LAYOUT_B,
                onSelected = { selectedLayout = AlbumLayoutType.LAYOUT_B },
            )
        }

        // 하단 버튼
        val isButtonEnabled = selectedLayout != null
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
                .clickable {
                    if (isButtonEnabled) {
                        navigateToDeliveryInfo(selectedLayout ?: AlbumLayoutType.LAYOUT_A)

                    }
                }
                .clip(RoundedCornerShape(14.dp))
                .background(if (isButtonEnabled) purple6C else purple6C.copy(alpha = 0.4f))
        ) {
            Text(
                text = stringResource(R.string.text_next),
                color = Color.White,
                fontSize = 14.sp,
                style = PetgrowTheme.typography.medium,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 14.dp)
                    .align(Alignment.Center)
            )
        }
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
            .height(240.dp)
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
                .fillMaxSize()
                .background(Color.White)
        ) {
            // 레이아웃 이미지
            val imageRes = when (layoutType) {
                AlbumLayoutType.LAYOUT_A -> {
                    if (isSelected) R.drawable.layout_1_selected else R.drawable.layout_1_unselected
                }
                AlbumLayoutType.LAYOUT_B -> {
                    if (isSelected) R.drawable.layout_2_selected else R.drawable.layout_2_unselected
                }
            }

            Image(
                painter = painterResource(imageRes),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}
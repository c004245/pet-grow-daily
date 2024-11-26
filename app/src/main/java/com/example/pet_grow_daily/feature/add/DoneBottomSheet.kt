package com.example.pet_grow_daily.feature.add

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DoneBottomSheet(onDone: () -> Unit, modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isVisible = true
    }

    // 부모 높이에 기반한 최하단 시작 지점 계산
    val density = LocalDensity.current
    val bottomOffsetPx = with(density) { 580.dp.roundToPx() } // 580dp를 px로 변환

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(580.dp)
            .padding(16.dp)

    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = slideInVertically(
                initialOffsetY = { bottomOffsetPx },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )))

        {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally, // Column 내 아이템들을 수평 중앙으로 정렬
                verticalArrangement = Arrangement.Center // Column 내 아
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_save_check),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.text_save_done),
                    style = PetgrowTheme.typography.bold,
                    color = black21,
                    textAlign = TextAlign.Center, // 텍스트 중앙 정렬


                )
            }

        }
    }
}
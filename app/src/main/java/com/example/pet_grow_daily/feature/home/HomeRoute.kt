package com.example.pet_grow_daily.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayDE
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

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
        EmptyTodayGrowRecordWidget(
            modifier = Modifier.padding(40.dp),
            isFullHeight = true
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(id = R.string.text_not_today_grow),
                    style = PetgrowTheme.typography.medium,
                    color = gray86

                )
            }
        }

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

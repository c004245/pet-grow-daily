package com.example.pet_grow_daily.feature.main.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.Gray900
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SplashScreen(
    navigateToDailyGrow: () -> Unit = {},
    viewModel: SplashViewModel = hiltViewModel()
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.showSplash()
    }

    LaunchedEffect(viewModel.sideEffects, lifecycleOwner) {
        viewModel.sideEffects.flowWithLifecycle(lifecycle = lifecycleOwner.lifecycle)
            .collect { sideEffect ->
                when (sideEffect) {
                    is SplashSideEffect.NavigateToDailyGrow -> navigateToDailyGrow()
                }
            }
    }

    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = true,
            transformColorForLightContent = { Gray900 }
        )
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = true,
            navigationBarContrastEnforced = false
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(purple6C),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_splash),
            contentDescription = "splash_icon"
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen()
}

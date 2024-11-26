package com.example.pet_grow_daily.feature.main.splash

sealed class SplashSideEffect {
    data object NavigateToDailyGrow: SplashSideEffect()
    data object NavigateToOnBoarding: SplashSideEffect()
}
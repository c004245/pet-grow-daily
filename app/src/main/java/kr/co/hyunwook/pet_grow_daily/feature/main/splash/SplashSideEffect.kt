package kr.co.hyunwook.pet_grow_daily.feature.main.splash

sealed class SplashSideEffect {
    data object NavigateToAlbum: SplashSideEffect()
    data object NavigateToOnBoarding: SplashSideEffect()
}
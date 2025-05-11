package kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.OnBoardingScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.onboardingNavGraph(
    navigateToAlbum: () -> Unit
) {
    composable<OnBoarding> {
        OnBoardingScreen(
            navigateToAlbum = navigateToAlbum
        )
    }
}

@Serializable
data object OnBoarding: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding"
}
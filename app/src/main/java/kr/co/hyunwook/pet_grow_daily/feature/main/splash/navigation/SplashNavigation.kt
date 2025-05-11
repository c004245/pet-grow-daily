package kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.SplashScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.splashNavGraph(
    navigateToAlbum: () -> Unit,
    navigateToOnBoarding: () -> Unit
) {
    composable<Splash> {
        SplashScreen(
            navigateToAlbum = navigateToAlbum,
            navigateToOnBoarding = navigateToOnBoarding
        )
    }
}

@Serializable
data object Splash: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.Splash"
}

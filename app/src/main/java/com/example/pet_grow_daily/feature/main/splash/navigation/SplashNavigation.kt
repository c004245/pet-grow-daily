package com.example.pet_grow_daily.feature.main.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.pet_grow_daily.core.navigation.Route
import com.example.pet_grow_daily.feature.main.splash.SplashScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.splashNavGraph(
    navigateToHome: () -> Unit
) {
    composable<Splash> {
        SplashScreen(
            navigateToHome = navigateToHome
        )
    }
}

@Serializable
data object Splash: Route
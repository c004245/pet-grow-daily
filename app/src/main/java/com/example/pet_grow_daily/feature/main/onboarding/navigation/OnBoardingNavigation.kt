package com.example.pet_grow_daily.feature.main.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.pet_grow_daily.core.navigation.Route
import com.example.pet_grow_daily.feature.main.onboarding.OnBoardingScreen
import kotlinx.serialization.Serializable


fun NavGraphBuilder.onboardingNavGraph(
    navigateToDailyGrow: () -> Unit
) {
    composable<OnBoarding> {
        OnBoardingScreen(
            navigateToDailyGrow = navigateToDailyGrow
        )
    }
}

@Serializable
data object OnBoarding: Route {
    override val route = "com.example.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding"
}
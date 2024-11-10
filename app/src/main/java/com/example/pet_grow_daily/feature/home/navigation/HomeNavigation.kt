package com.example.pet_grow_daily.feature.home.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.pet_grow_daily.core.navigation.MainTabRoute
import com.example.pet_grow_daily.feature.home.HomeRoute
import kotlinx.serialization.Serializable

fun NavController.navigateHome(
    navOptions: NavOptions
) {
    navigate(Home, navOptions = navOptions)
}

fun NavGraphBuilder.homeNavGraph(
    paddingValues: PaddingValues,
) {
    composable<Home> {
        HomeRoute(
            paddingValues = paddingValues
        )
    }

}

@Serializable
data object Home: MainTabRoute
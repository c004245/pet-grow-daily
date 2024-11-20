package com.example.pet_grow_daily.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pet_grow_daily.feature.dailygrow.navigation.DailyGrow
import com.example.pet_grow_daily.feature.main.splash.navigation.Splash
import com.example.pet_grow_daily.feature.total.navigation.Total


class MainNavigator(
    val navController: NavHostController
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = Splash

    fun navigateToDailyGrow(
        navOptions: NavOptions,
    ) {
        navController.navigate(DailyGrow, navOptions = navOptions)
    }


    fun navigateToTotal(
        navOptions: NavOptions
    ) {
        navController.navigate(Total, navOptions = navOptions)
    }
}

@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
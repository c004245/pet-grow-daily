package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.dailygrow.navigation.DailyGrow
import kr.co.hyunwook.pet_grow_daily.feature.main.name.navigation.Name
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.Splash
import kr.co.hyunwook.pet_grow_daily.feature.total.navigation.Total


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

    fun navigateToName(
        navOptions: NavOptions
    ) {
        navController.navigate(Name, navOptions = navOptions)
    }

    fun navigateToOnBoarding(
        navOptions: NavOptions
    ) {
        navController.navigate(OnBoarding, navOptions = navOptions)
    }

    @Composable
    fun isSplashOrOnBoardingScreen(): Boolean {
        val currentRoute = findRouteFromDestination(currentDestination?.route)
        return currentRoute == Splash || currentRoute == OnBoarding || currentRoute == Name
    }
}

fun findRouteFromDestination(route: String?): Route? {
    return when (route) {
        Splash.route -> Splash
        DailyGrow.route -> DailyGrow
        Total.route -> Total
        OnBoarding.route -> OnBoarding
        Name.route -> Name
        else -> null
    }
}


@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
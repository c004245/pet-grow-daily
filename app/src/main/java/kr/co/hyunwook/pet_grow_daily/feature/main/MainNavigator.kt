package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.add.navigation.Add
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.Album
import kr.co.hyunwook.pet_grow_daily.feature.main.name.navigation.Name
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.Splash
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.MyPage
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWrite
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWriteTab
import kr.co.hyunwook.pet_grow_daily.feature.total.navigation.Total
import android.util.Log


class MainNavigator(
    val navController: NavHostController
) {
    private val currentDestination: NavDestination?
        @Composable get() = navController
            .currentBackStackEntryAsState().value?.destination

    val startDestination = Splash

    fun navigateToAlbum(
        navOptions: NavOptions,
    ) {
        navController.navigate(Album, navOptions = navOptions)
    }


    fun navigateToTotal(
        navOptions: NavOptions
    ) {
        navController.navigate(Total, navOptions = navOptions)
    }

    fun navigateToOrder(
        navOptions: NavOptions
    ) {
        navController.navigate(Order, navOptions = navOptions)
    }

    fun navigateToName(
        navOptions: NavOptions
    ) {
        navController.navigate(Name, navOptions = navOptions)
    }

    fun navigateToMyPage(
        navOptions: NavOptions
    ) {
        navController.navigate(MyPage, navOptions = navOptions)
    }
    fun navigateToAdd(
        navOptions: NavOptions
    ) {
        navController.navigate(Add, navOptions = navOptions)
    }


    fun navigateToOnBoarding(
        navOptions: NavOptions
    ) {
        navController.navigate(OnBoarding, navOptions = navOptions)
    }

    fun navigateToRecordWrite(
        selectedImageUris: List<String>,
        navOptions: NavOptions? = null
    ) {
        navController.navigate(
            RecordWrite.createRoute(selectedImageUris), navOptions = navOptions)
    }

    @Composable
    fun isShowBottomBar(): Boolean {
        val currentRoute = findRouteFromDestination(currentDestination?.route)
        Log.d("HWO", "currentRoute -> $currentRoute")
        return currentRoute == Album || currentRoute == Order || currentRoute == MyPage
    }
}

fun findRouteFromDestination(route: String?): Route? {
    Log.d("HWO", "findRouteFrom -> $route")
    return when (route) {
        Splash.route -> Splash
        Album.route -> Album
        Total.route -> Total
        OnBoarding.route -> OnBoarding
        MyPage.route -> MyPage
        Name.route -> Name
        Add.route -> Add
        Order.route -> Order
        RecordWriteTab.route -> RecordWriteTab
        else -> null
    }
}


@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
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
import kr.co.hyunwook.pet_grow_daily.feature.anotherpet.navigation.AnotherPet
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.DeliveryAdd
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.DeliveryCheck
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.DeliveryList
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.DeliveryRegister
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding
import kr.co.hyunwook.pet_grow_daily.feature.main.profile.navigation.Profile
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.Splash
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.Alarm
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.MyPage
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumSelect
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.OrderProductList
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWrite
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWriteTab
import kr.co.hyunwook.pet_grow_daily.feature.total.navigation.Total
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import com.google.gson.Gson
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.OrderDone
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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

    fun navigateToOnBoarding(
        navOptions: NavOptions
    ) {
        navController.navigate(OnBoarding, navOptions = navOptions)
    }

    fun navigateToOrder(
        orderProduct: OrderProduct,
        navOptions: NavOptions
    ) {
        val gson = Gson()
        val orderProductJson = gson.toJson(orderProduct)
        val encodedJson = URLEncoder.encode(orderProductJson, StandardCharsets.UTF_8.toString())
        navController.navigate(Order(encodedJson), navOptions = navOptions)
    }


    fun navigateToOrderProductList(
        navOptions: NavOptions
    ) {
        navController.navigate(OrderProductList, navOptions)
    }
    fun navigateToAnotherPet(
        navOptions: NavOptions
    ) {
        navController.navigate(
            AnotherPet, navOptions = navOptions
        )
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

    fun navigateToProfile(
        navOptions: NavOptions
    ) {
        navController.navigate(Profile, navOptions = navOptions)
    }

    fun navigateToDeliveryList(
        navOptions: NavOptions
    ) {
        navController.navigate(DeliveryList, navOptions = navOptions)
    }

    fun navigateToDeliveryAdd(
        deliveryId: Int? = null,
        navOptions: NavOptions? = null
    ) {
        navController.navigate(DeliveryAdd(deliveryId), navOptions = navOptions)
    }

    fun navigateToDeliveryRegister(
        navOptions: NavOptions
    ) {
        navController.navigate(DeliveryRegister, navOptions = navOptions)
    }

    fun navigateToDeliveryCheck(
        navOptions: NavOptions
    ) {
        navController.navigate(DeliveryCheck, navOptions = navOptions)
    }



    fun navigateToRecordWrite(
        selectedImageUris: List<String>,
        navOptions: NavOptions? = null
    ) {
        navController.navigate(
            RecordWrite.createRoute(selectedImageUris), navOptions = navOptions)
    }

    fun navigateToAlbumSelect(
        navOptions: NavOptions
    ) {
        navController.navigate(AlbumSelect, navOptions)
    }

    fun navigateToAlarm(
        navOptions: NavOptions
    ) {
        navController.navigate(Alarm, navOptions)
    }


    fun navigateToOrderDone(
        navOptions: NavOptions
    ) {
        navController.navigate(OrderDone, navOptions)
    }
    @Composable
    fun isShowBottomBar(): Boolean {
        val currentRoute = findRouteFromDestination(currentDestination?.route)
        return currentRoute == Album || currentRoute == OrderProductList || currentRoute == MyPage || currentRoute == AnotherPet
    }
}

fun findRouteFromDestination(route: String?): Route? {
    return when (route) {
        Splash.route -> Splash
        Album.route -> Album
        Total.route -> Total
        OnBoarding.route -> OnBoarding
        MyPage.route -> MyPage
        Profile.route -> Profile
        Add.route -> Add
        RecordWriteTab.route -> RecordWriteTab
        AnotherPet.route -> AnotherPet
        DeliveryList.route -> DeliveryList
        OrderProductList.route -> OrderProductList
        DeliveryAdd().route -> DeliveryAdd()
        else -> null
    }
}


@Composable
internal fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}

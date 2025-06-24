package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.MyPageRoute
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.DeliveryRoute
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.MyPage
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.deliveryNavGraph(
) {
    composable<Delivery> {
        DeliveryRoute(
        )
    }
}

@Serializable
data object Delivery: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.Delivery"
}
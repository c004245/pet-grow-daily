package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.DeliveryListRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.DeliveryAddRoute

fun NavGraphBuilder.deliveryListNavGraph(
    navigateToMyPage: () -> Unit,
    navigateToDeliveryAdd: () -> Unit
) {
    composable<DeliveryList> {
        DeliveryListRoute(
            navigateToMyPage = navigateToMyPage,
            navigateToDeliveryAdd = navigateToDeliveryAdd
        )
    }
}

fun NavGraphBuilder.deliveryAddNavGraph(
    navigateToDeliveryList: () -> Unit
) {
    composable<DeliveryAdd> {
        DeliveryAddRoute()
    }
}

@Serializable
data object DeliveryList: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryList"
}

@Serializable
data object DeliveryAdd : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryAdd"
}

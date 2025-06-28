package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.DeliveryListRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.DeliveryAddRoute

fun NavGraphBuilder.deliveryListNavGraph(
    navigateToMyPage: () -> Unit,
    navigateToDeliveryAdd: (Int?) -> Unit
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
    composable<DeliveryAdd> { backStackEntry ->
        val args = backStackEntry.toRoute<DeliveryAdd>()
        DeliveryAddRoute(
            deliveryId = args.deliveryId,
            navigateToDeliveryList = navigateToDeliveryList
        )
    }
}

@Serializable
data object DeliveryList : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryList"
}

@Serializable
data class DeliveryAdd(val deliveryId: Int? = null) : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryAdd"
}

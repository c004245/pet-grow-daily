package kr.co.hyunwook.pet_grow_daily.feature.order.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumSelectRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderProductListRoute
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderProductType
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel


fun NavGraphBuilder.orderProductListGraph(
    navigateToOrder: (OrderProductType) -> Unit,
    viewModel: OrderViewModel
) {
    composable<OrderProductList> {
        OrderProductListRoute(
            navigateToOrder = navigateToOrder,
            viewModel = viewModel
        )
    }

}
fun NavGraphBuilder.orderNavGraph(
    navigateToAlbumSelect: () -> Unit,
    viewModel: OrderViewModel
) {
    composable<Order> { backStackEntry ->
        val args = backStackEntry.toRoute<Order>()
        OrderRoute (
            navigateToAlbumSelect = navigateToAlbumSelect,
            viewModel = viewModel,
            orderProductType = args.orderProductType
        )
    }
}

fun NavGraphBuilder.albumSelectNavGraph(
    navigateToDeliveryCheck: () -> Unit,
    navigateToDeliveryRegister: () -> Unit,
    viewModel: OrderViewModel
) {
    composable<AlbumSelect> { backStackEntry ->
        AlbumSelectRoute(
            viewModel = viewModel,
            navigateToDeliveryCheck = navigateToDeliveryCheck,
            navigateToDeliveryRegister = navigateToDeliveryRegister
        )
    }
}

@Serializable
data class Order(val orderProductType: OrderProductType) : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order"
}

@Serializable
data object OrderProductList: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.OrderProductList"
}
@Serializable
data object AlbumSelect: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumSelect"

}
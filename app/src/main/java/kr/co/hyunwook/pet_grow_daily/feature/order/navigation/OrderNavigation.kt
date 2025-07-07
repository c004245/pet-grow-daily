package kr.co.hyunwook.pet_grow_daily.feature.order.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumSelectRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel

fun NavGraphBuilder.orderNavGraph(
    navigateToAlbumSelect: () -> Unit,
    viewModel: OrderViewModel
) {
    composable<Order> { backStackEntry ->
        OrderRoute (
            navigateToAlbumSelect = navigateToAlbumSelect,
            viewModel = viewModel
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
data object Order: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order"
}

@Serializable
data object AlbumSelect: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumSelect"

}
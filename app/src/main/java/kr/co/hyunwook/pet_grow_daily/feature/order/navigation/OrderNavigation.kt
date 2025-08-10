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
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import com.google.gson.Gson
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumLayoutRoute
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderDoneRoute
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

fun NavGraphBuilder.orderProductListGraph(
    navigateToOrder: (OrderProduct) -> Unit,
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
    onBackClick: () -> Unit,
    viewModel: OrderViewModel
) {
    composable<Order> { backStackEntry ->
        val args = backStackEntry.toRoute<Order>()
        val decodedJson =
            URLDecoder.decode(args.orderProductJson, StandardCharsets.UTF_8.toString())
        val orderProduct = Gson().fromJson(decodedJson, OrderProduct::class.java)
        OrderRoute (
            navigateToAlbumSelect = { navigateToAlbumSelect() },
            viewModel = viewModel,
            orderProduct = orderProduct,
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.albumSelectNavGraph(
//    navigateToDeliveryCheck: () -> Unit,
//    navigateToDeliveryRegister: () -> Unit,
    navigateToAlbumLayout: () -> Unit,
    viewModel: OrderViewModel,
    onBackClick: () -> Unit

) {
    composable<AlbumSelect> { backStackEntry ->
        AlbumSelectRoute(
            viewModel = viewModel,
//            navigateToDeliveryCheck = navigateToDeliveryCheck,
//            navigateToDeliveryRegister = navigateToDeliveryRegister,
            navigateToAlbumLayout = navigateToAlbumLayout,
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.albumLayoutNavGraph(
    navigateToDeliveryCheck: () -> Unit,
    navigateToDeliveryRegister: () -> Unit,
    viewModel: OrderViewModel,
    onBackClick: () -> Unit
) {
    composable<AlbumLayout> { backStackEntry ->
        AlbumLayoutRoute(
            viewModel = viewModel,
            navigateToDeliveryCheck = navigateToDeliveryCheck,
            navigateToDeliveryRegister = navigateToDeliveryRegister,
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.orderDoneNavGraph(
    navigateToAlbum: () -> Unit
) {
    composable<OrderDone> { backStackEntry ->
        OrderDoneRoute(
            navigateToAlbum = navigateToAlbum
        )
    }

}

@Serializable
data class Order(val orderProductJson: String) : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order"
}

@Serializable
data object OrderProductList: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.OrderProductList"
}
@Serializable
data object AlbumSelect : Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumSelect"
}

@Serializable
data object AlbumLayout: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.AlbumLayout"
}

@Serializable
data object OrderDone: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.OrderDone"
}
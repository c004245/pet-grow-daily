package kr.co.hyunwook.pet_grow_daily.feature.order.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
    composable<Order>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            // Back to MyPage: enter from left
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            // Back stack popping: exit to left
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->

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
    navigateToAlbumLayout: () -> Unit,
    viewModel: OrderViewModel,
    onBackClick: () -> Unit

) {
    composable<AlbumSelect>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            // Back to MyPage: enter from left
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            // Back stack popping: exit to left
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->
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
    composable<AlbumLayout>(
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) { backStackEntry ->
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
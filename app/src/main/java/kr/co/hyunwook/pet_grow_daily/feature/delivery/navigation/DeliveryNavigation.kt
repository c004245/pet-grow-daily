package kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation

import DeliveryCheckRoute
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryListRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryAddRoute
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryRegisterRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.toRoute
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel

fun NavGraphBuilder.deliveryListNavGraph(
    navigateToMyPage: () -> Unit,
    navigateToDeliveryAdd: (Int?) -> Unit
) {
    composable<DeliveryList>(
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
    ) {
        DeliveryListRoute(
            navigateToMyPage = navigateToMyPage,
            navigateToDeliveryAdd = navigateToDeliveryAdd
        )
    }
}

fun NavGraphBuilder.deliveryAddNavGraph(
    navigateToDeliveryList: () -> Unit
) {
    composable<DeliveryAdd>(
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
        val args = backStackEntry.toRoute<DeliveryAdd>()

        DeliveryAddRoute(
            deliveryId = args.deliveryId,
            navigateToDeliveryList = navigateToDeliveryList
        )
    }
}

fun NavGraphBuilder.deliveryCheckNavGraph(
    viewModel: OrderViewModel,
    navigateToOrderDone: () -> Unit,
    onBackClick: () -> Unit
) {
    composable<DeliveryCheck> { backStackEntry ->
        DeliveryCheckRoute(
            viewModel = viewModel,
            navigateToOrderDone = navigateToOrderDone,
            onBackClick = onBackClick
        )
    }
}

fun NavGraphBuilder.deliveryRegisterNavGraph(
    viewModel: OrderViewModel,
    navigateToOrderDone: () -> Unit
) {
    composable<DeliveryRegister> { backStackEntry ->
        DeliveryRegisterRoute(
            viewModel = viewModel,
            navigateToOrderDone = navigateToOrderDone
        )
    }
}

@Serializable
data object DeliveryList : Route {
    override val route =
        "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryList"
}

@Serializable
data class DeliveryAdd(val deliveryId: Int? = null) : Route {
    override val route =
        "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryAdd"
}

@Serializable
data object DeliveryRegister : Route {
    override val route =
        "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryRegister"
}

@Serializable
data object DeliveryCheck : Route {
    override val route =
        "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryCheck"
}
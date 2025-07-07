package kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation

import DeliveryCheckRoute
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryListRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryAddRoute
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryRegisterRoute
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel

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

fun NavGraphBuilder.deliveryCheckNavGraph(
    viewModel: OrderViewModel
) {
    composable<DeliveryCheck> { backStackEntry ->
        DeliveryCheckRoute(
            viewModel = viewModel
        )
    }
}

fun NavGraphBuilder.deliveryRegisterNavGraph(
    viewModel: OrderViewModel
) {
    composable<DeliveryRegister> { backStackEntry ->
        DeliveryRegisterRoute(
            viewModel = viewModel
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


@Serializable
data object DeliveryRegister: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryRegister"
}


@Serializable
data object DeliveryCheck: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryCheck"
}
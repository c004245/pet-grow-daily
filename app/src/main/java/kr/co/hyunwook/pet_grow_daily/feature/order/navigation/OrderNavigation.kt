package kr.co.hyunwook.pet_grow_daily.feature.order.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumSelectRoute

fun NavGraphBuilder.orderNavGraph(
    navigateToAlbumSelect: () -> Unit
) {
    composable<Order> {
        OrderRoute (
            navigateToAlbumSelect = navigateToAlbumSelect
        )
    }
}

fun NavGraphBuilder.albumSelectNavGraph(

) {
    composable<AlbumSelect> {
        AlbumSelectRoute(

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
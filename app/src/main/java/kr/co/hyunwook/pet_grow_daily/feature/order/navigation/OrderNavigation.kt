package kr.co.hyunwook.pet_grow_daily.feature.order.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.orderNavGraph(

) {
    composable<Order> {
        OrderRoute (


        )
    }
}

@Serializable
data object Order: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.order.navigation.Order"
}
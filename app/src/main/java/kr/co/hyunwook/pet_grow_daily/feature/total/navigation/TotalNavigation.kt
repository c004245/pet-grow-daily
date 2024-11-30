package kr.co.hyunwook.pet_grow_daily.feature.total.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.feature.total.TotalRoute
import kotlinx.serialization.Serializable


fun NavController.navigateTotal(
    navOptions: NavOptions
) {
    navigate(Total, navOptions = navOptions)
}

fun NavGraphBuilder.totalNavGraph() {
    composable<Total> {
        TotalRoute()
    }
}


@Serializable
data object Total: MainTabRoute {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.main.total.navigation.Total"

}

package com.example.pet_grow_daily.feature.total.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.pet_grow_daily.core.navigation.MainTabRoute
import com.example.pet_grow_daily.feature.main.MainTab
import com.example.pet_grow_daily.feature.total.TotalRoute
import kotlinx.serialization.Serializable


fun NavController.navigateTotal(
    navOptions: NavOptions
) {
    navigate(Total, navOptions = navOptions)
}

fun NavGraphBuilder.totalNavGraph(
    paddingValues: PaddingValues
) {
    composable<Total> {
        TotalRoute(
            paddingValues = paddingValues
        )
    }
}


@Serializable
data object Total: MainTabRoute {
    override val route = "com.example.pet_grow_daily.feature.main.total.navigation.Total"

}

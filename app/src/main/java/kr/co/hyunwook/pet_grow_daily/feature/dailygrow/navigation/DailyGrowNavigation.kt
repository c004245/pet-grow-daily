package kr.co.hyunwook.pet_grow_daily.feature.dailygrow.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.feature.dailygrow.DailyGrowRoute
import kotlinx.serialization.Serializable



fun NavGraphBuilder.dailyGrowNavGraph(
    paddingValues: PaddingValues
) {
    composable<DailyGrow> {
        DailyGrowRoute(
            paddingValues = paddingValues
        )
    }

}

@Serializable
data object DailyGrow: MainTabRoute {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.main.dailygrow.navigation.DailyGrow"
}

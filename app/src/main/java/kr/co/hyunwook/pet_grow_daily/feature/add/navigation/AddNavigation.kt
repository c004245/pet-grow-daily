package kr.co.hyunwook.pet_grow_daily.feature.add.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.add.AddRoute
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumRoute
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.Album


fun NavGraphBuilder.addNavGraph(
    navigateToRecordWrite: (List<String>) -> Unit,
    onBackClick: () -> Unit
) {
    composable<Add>(
        enterTransition = {
            slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        AddRoute(
            navigateToRecordWrite = navigateToRecordWrite,
            onBackClick = onBackClick
        )
    }

}

@Serializable
data object Add: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.add.navigation.Add"
}

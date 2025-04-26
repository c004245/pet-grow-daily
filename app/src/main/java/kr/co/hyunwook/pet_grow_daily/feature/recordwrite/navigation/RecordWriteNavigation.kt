package kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.RecordWriteRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.recordWriteGraph(
    navigateToAlbum: () -> Unit
) {
    composable<RecordWrite> {
        RecordWriteRoute(
            navigateToAlbum = navigateToAlbum
        )
    }

}

@Serializable
data object RecordWrite: MainTabRoute {
    override val route: String = "kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWrite"
}
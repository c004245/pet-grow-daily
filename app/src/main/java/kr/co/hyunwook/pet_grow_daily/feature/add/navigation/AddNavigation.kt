package kr.co.hyunwook.pet_grow_daily.feature.add.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.feature.add.AddRoute
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumRoute
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.Album


fun NavGraphBuilder.addNavGraph(
    navigateToRecordWrite: (List<String>) -> Unit
) {
    composable<Add> {
        AddRoute(
            navigateToRecordWrite = navigateToRecordWrite
        )
    }

}

@Serializable
data object Add: MainTabRoute {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.add.navigation.Add"
}

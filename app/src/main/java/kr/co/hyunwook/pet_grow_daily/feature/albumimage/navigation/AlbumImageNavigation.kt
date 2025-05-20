package kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.AlbumImageRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.albumImageGraph(
    navigateToAlbum: () -> Unit
) {
    composable<AlbumImage> {
        AlbumImageRoute(
        )
    }

}

@Serializable
data object AlbumImage: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation.AlbumImage"
}

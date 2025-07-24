package kr.co.hyunwook.pet_grow_daily.feature.album.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumRoute


fun NavGraphBuilder.albumNavGraph(
    paddingValues: PaddingValues,
    navigateToAdd: () -> Unit,
    navigateToAnotherPet: () -> Unit,
    navigateToOrderProductList: () -> Unit
) {
    composable<Album> {
        AlbumRoute(
            paddingValues = paddingValues,
            navigateToAdd = navigateToAdd,
            navigateToAnotherPet = navigateToAnotherPet,
            navigateToOrderProductList = navigateToOrderProductList,
        )
    }

}

@Serializable
data object Album: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.album.navigation.Album"
}

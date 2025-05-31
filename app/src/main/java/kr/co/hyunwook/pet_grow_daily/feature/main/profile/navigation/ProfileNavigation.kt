package kr.co.hyunwook.pet_grow_daily.feature.main.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.feature.main.profile.ProfileScreen

fun NavGraphBuilder.profileNavGraph(
    navigateToAlbum: () -> Unit
) {
    composable<Profile> {
        ProfileScreen(
            navigateToAlbum = navigateToAlbum
        )
    }
}

@Serializable
data object Profile: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.main.profile.navigation.Profile"

}
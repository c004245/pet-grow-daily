package kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.RecordWriteRoute
import android.net.Uri
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.recordWriteGraph(
    navigateToAlbum: () -> Unit,
    onBackClick: () -> Unit
) {
    composable(
        route = "record-write?selectedImageUris={selectedImageUris}",
        arguments = listOf(
            navArgument("selectedImageUris") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }
        ),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {backStackEntry ->
        val encodedUris = backStackEntry.arguments?.getString("selectedImageUris") ?: ""
        val selectedImageUris = if (encodedUris.isNotEmpty()) {
            try {
                Json.decodeFromString<List<String>>(Uri.decode(encodedUris))
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        RecordWriteRoute(
            selectedImageUris = selectedImageUris,
            navigateToAlbum = navigateToAlbum,
            onBackClick = onBackClick
        )
    }

}

object RecordWrite {
    const val baseRoute = "record-write"

    fun createRoute(selectedImageUris: List<String>): String {
        val urisJson = Json.encodeToString(selectedImageUris)
        val encodedUris = Uri.encode(urisJson)
        return "$baseRoute?selectedImageUris=$encodedUris"
    }
}

@Serializable
data object RecordWriteTab: Route {
    override val route: String = "kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWrite"
}
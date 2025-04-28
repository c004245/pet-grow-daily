package kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.RecordWriteRoute
import android.net.Uri
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

fun NavGraphBuilder.recordWriteGraph(
    navigateToAlbum: () -> Unit
) {
    composable(
        route = "record-write?selectedImageUris={selectedImageUris}",
        arguments = listOf(
            navArgument("selectedImageUris") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }
        )
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
            navigateToAlbum = navigateToAlbum
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
data object RecordWriteTab: MainTabRoute {
    override val route: String = "kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.RecordWrite"
}
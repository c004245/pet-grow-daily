package com.example.pet_grow_daily.feature.main.name.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.pet_grow_daily.core.navigation.Route
import com.example.pet_grow_daily.feature.main.name.NameScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.nameNavaGraph(
    navigateToDailyGrow: () -> Unit
) {
    composable<Name> {
        NameScreen(
            navigateToDailyGrow = navigateToDailyGrow
        )
    }
}

@Serializable
data object Name: Route {
    override val route = "com.example.pet_grow_daily.feature.main.name.navigation.Name"

}
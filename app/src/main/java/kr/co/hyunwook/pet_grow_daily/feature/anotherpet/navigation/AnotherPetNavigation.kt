package kr.co.hyunwook.pet_grow_daily.feature.anotherpet.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.anotherpet.AnotherPetRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.anotherPetGraph(

) {
    composable<AnotherPet> {
        AnotherPetRoute(

        )
    }
}

@Serializable
data object AnotherPet: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.anotherpet.navigation.AnotherPet"
}
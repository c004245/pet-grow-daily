package kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.MyPageRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

fun NavGraphBuilder.myPageNavigation(
    paddingValues: PaddingValues
) {
    composable<MyPage> {
        MyPageRoute(
            paddingValues = paddingValues
        )
    }
}

@Serializable
data object MyPage: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.MyPage"
}
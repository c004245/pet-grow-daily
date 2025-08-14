package kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.MyPageRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.mypage.AlarmRoute
import kr.co.hyunwook.pet_grow_daily.feature.mypage.BusinessInfoRoute

fun NavGraphBuilder.myPageNavGraph(
    navigateToDeliveryList: () -> Unit,
    navigateToAlarm: () -> Unit,
    navigateToBusinessInfo: () -> Unit,
) {
    composable<MyPage> {
        MyPageRoute(
            onClickDeliveryList = navigateToDeliveryList,
            onClickAlarm = navigateToAlarm,
            onClickBusinessInfo = navigateToBusinessInfo,
        )
    }
}

fun NavGraphBuilder.alarmNavGraph(
    navigateToMyPage: () -> Unit
) {

    composable<Alarm>(
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
        },
        popEnterTransition = {
            // Back to MyPage: enter from left
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            // Back stack popping: exit to left
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        AlarmRoute(
            navigateToMyPage = navigateToMyPage,
            modifier = Modifier.zIndex(1f)
        )
    }
}


fun NavGraphBuilder.businessInfoNavGraph(
    navigateToMyPage: () -> Unit
) {

    composable<BusinessInfo>(
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
        },
        popEnterTransition = {
            // Back to MyPage: enter from left
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            // Back stack popping: exit to left
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300)
            )
        }
    ) {
        BusinessInfoRoute(
            navigateToMyPage = navigateToMyPage,
            modifier = Modifier.zIndex(1f)
        )
    }
}


@Serializable
data object MyPage: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.MyPage"
}

@Serializable
data object Alarm: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.Alarm"
}

@Serializable
data object BusinessInfo: Route {
    override val route = "kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.BusinessInfo"
}
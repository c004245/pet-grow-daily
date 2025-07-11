package kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation

import kotlinx.serialization.Serializable
import kr.co.hyunwook.pet_grow_daily.core.navigation.MainTabRoute
import kr.co.hyunwook.pet_grow_daily.core.navigation.Route
import kr.co.hyunwook.pet_grow_daily.feature.mypage.MyPageRoute
import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kr.co.hyunwook.pet_grow_daily.feature.mypage.AlarmRoute

fun NavGraphBuilder.myPageNavGraph(
    navigateToDeliveryList: () -> Unit,
    navigateToAlarm: () -> Unit
) {
    composable<MyPage> {
        MyPageRoute(
            onClickDeliveryList = navigateToDeliveryList,
            onClickAlarm = navigateToAlarm
        )
    }
}

fun NavGraphBuilder.alarmNavGraph(
    navigateToMyPage: () -> Unit
) {

    composable<Alarm> {
        AlarmRoute(
            navigateToMyPage = navigateToMyPage
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
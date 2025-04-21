package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.main.name.navigation.nameNavaGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.onboardingNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.splashNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.total.navigation.totalNavGraph
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.albumNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.myPageNavigation
import androidx.compose.foundation.shape.RoundedCornerShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var isSheetOpen by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(SelectTab.ALBUM) }


    LaunchedEffect(Unit) {
        viewModel.saveDoneEvent.collect { isSuccess ->
            if (isSuccess) {
//                Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    sheetState.hide()
                }
                isSheetOpen = false
            }
        }
    }


    Scaffold(
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navigator.navController,
                    startDestination = navigator.startDestination,
                    enterTransition = { EnterTransition.None },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { ExitTransition.None }
                ) {
                    splashNavGraph(
                        navigateToDailyGrow = {
                            navigate(navigator, SelectTab.ALBUM)
                        },
                        navigateToOnBoarding = {
                            navigate(navigator)
                        }
                    )
                    albumNavGraph(
                        paddingValues = paddingValues
                    )
                    totalNavGraph()

                    myPageNavigation(
                        paddingValues = paddingValues
                    )

                    onboardingNavGraph(
                        navigateToName = {
//                            navigate(navigator, SelectTab.NAME)
                        }
                    )
                    nameNavaGraph(
                        navigateToDailyGrow = {
//                            navigate(navigator, SelectTab.DAILYGROW)
                        }
                    )


                }
            }
        },
        bottomBar = {
            if (!navigator.isSplashOrOnBoardingScreen()) {
                CustomBottomBar(
                    onAlbumClick = {
                        selectedTab = SelectTab.ALBUM
                        navigate(navigator, SelectTab.ALBUM)
//                        coroutineScope.launch {
//                            sheetState.show()
//                        }
//                        isSheetOpen = true
                    },
                    onOrderClick = {
                        selectedTab = SelectTab.ORDER
                        navigate(navigator, SelectTab.ORDER)
                    },
                    onMyPageClick = {
                        selectedTab = SelectTab.MYPAGE
                        navigate(navigator, SelectTab.MYPAGE)
                    },
                    selectedTab = selectedTab
                )

            }

        }
    )
//    if (isSheetOpen) {
//        ModalBottomSheet(
//            onDismissRequest = {
//                isSheetOpen = false
//            },
//            sheetState = sheetState
//        ) {
//            BottomSheetContent(
//                onCloseClick = { record ->
//                    viewModel.saveGrowRecord(record)
//                }
//            )
//        }
//    }
}

fun navigate(navigator: MainNavigator, selectTab: SelectTab? = null) {
    val navOptions = navOptions {
        popUpTo(navigator.navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
    when (selectTab) {
        SelectTab.ALBUM -> {
            navigator.navigateToAlbum(navOptions = navOptions)
        }
        SelectTab.ORDER -> {
            navigator.navigateToTotal(navOptions = navOptions)
        }
        SelectTab.MYPAGE -> {
            navigator.navigateToMyPage(navOptions = navOptions)
        }
        else -> {
            navigator.navigateToOnBoarding(navOptions = navOptions)
        }
    }
}

@Composable
fun CustomBottomBar(
    onAlbumClick: () -> Unit,
    onOrderClick: () -> Unit,
    onMyPageClick: () -> Unit,
    selectedTab: SelectTab
) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(60.dp),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            onAlbumClick()
                        }
                        .weight(1f)

                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_album_tab
                        ),
                        tint = if (selectedTab == SelectTab.ALBUM) purple6C else gray86,
                        contentDescription = "Album",
                    )
                    Text(
                        text = stringResource(id = R.string.text_album_title),
                        style = PetgrowTheme.typography.medium,
                        fontSize = 11.sp,
                        color = if (selectedTab == SelectTab.ALBUM) purple6C else gray86
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            onOrderClick()
                        }
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_order_tab
                        ),
                        tint = if (selectedTab == SelectTab.ORDER) purple6C else gray86, // 상태에 따른 색상 변경
                        contentDescription = "order",
                    )
                    Text(
                        text = stringResource(id = R.string.text_order_title),
                        fontSize = 11.sp,
                        style = PetgrowTheme.typography.medium,
                        color = if (selectedTab == SelectTab.ORDER) purple6C else gray86
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            onMyPageClick()
                        }
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_mypage_tab
                        ),
                        tint = if (selectedTab == SelectTab.MYPAGE) purple6C else gray86, // 상태에 따른 색상 변경
                        contentDescription = "order",
                    )
                    Text(
                        text = stringResource(id = R.string.text_mypage_title),
                        fontSize = 11.sp,
                        style = PetgrowTheme.typography.medium,
                        color = if (selectedTab == SelectTab.MYPAGE) purple6C else gray86
                    )
                }
            }
        }

}


@Preview
@Composable
fun MainScreenPreview() {
    PetgrowTheme {
        MainScreen()
    }
}

enum class SelectTab {
    ALBUM, ORDER, MYPAGE
}


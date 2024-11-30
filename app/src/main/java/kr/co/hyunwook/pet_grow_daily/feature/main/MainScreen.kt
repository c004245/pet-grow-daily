package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.layout.ContentScale
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
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.add.BottomSheetContent
import kr.co.hyunwook.pet_grow_daily.feature.dailygrow.navigation.dailyGrowNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.name.navigation.nameNavaGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.onboardingNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.splashNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.total.navigation.totalNavGraph
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kotlinx.coroutines.launch

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

    var selectedTab by remember { mutableStateOf(SelectTab.DAILYGROW) }


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
                            navigate(navigator, SelectTab.DAILYGROW)
                        },
                        navigateToOnBoarding = {
                            navigate(navigator)
                        }
                    )
                    dailyGrowNavGraph(
                        paddingValues = paddingValues
                    )
                    totalNavGraph()
                    onboardingNavGraph(
                        navigateToName = {
                            navigate(navigator, SelectTab.NAME)
                        }
                    )
                    nameNavaGraph(
                        navigateToDailyGrow = {
                            navigate(navigator, SelectTab.DAILYGROW)
                        }
                    )


                }
            }
        },
        bottomBar = {
            if (!navigator.isSplashOrOnBoardingScreen()) {
                CustomBottomBar(
                    onSelectBottomClick = {
                        coroutineScope.launch {
                            sheetState.show()
                        }
                        isSheetOpen = true
                    },
                    onDailyGrowClick = {
                        selectedTab = SelectTab.DAILYGROW
                        navigate(navigator, SelectTab.DAILYGROW)
                    },
                    onTotalClick = {
                        selectedTab = SelectTab.TOTAL
                        navigate(navigator, SelectTab.TOTAL)
                    },
                    selectedTab = selectedTab
                )

            }

        }
    )
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = {
                isSheetOpen = false
            },
            sheetState = sheetState
        ) {
            BottomSheetContent(
                onCloseClick = { record ->
                    viewModel.saveGrowRecord(record)
                }
            )
        }
    }

}

fun navigate(navigator: MainNavigator, selectTab: SelectTab? = null) {
    val navOptions = navOptions {
        popUpTo(navigator.navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
    when (selectTab) {
        SelectTab.DAILYGROW -> {
            navigator.navigateToDailyGrow(navOptions = navOptions)
        }
        SelectTab.TOTAL -> {
            navigator.navigateToTotal(navOptions = navOptions)
        }
        SelectTab.NAME -> {
            navigator.navigateToName(navOptions = navOptions)
        }
        else -> {
            navigator.navigateToOnBoarding(navOptions = navOptions)
        }
    }
}

@Composable
fun CustomBottomBar(
    onSelectBottomClick: () -> Unit,
    onDailyGrowClick: () -> Unit,
    onTotalClick: () -> Unit,
    selectedTab: SelectTab
) {
//    var selectedTab by remember { mutableStateOf(SelectTab.DAILYGROW) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(80.dp)
    ) {
        Surface(
            color = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp)
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
                            onDailyGrowClick()
                        }
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_dailygrow_select_tab
                        ),
                        tint = if (selectedTab == SelectTab.DAILYGROW) purple6C else black21,
                        contentDescription = "Today's Growth",
                    )
                    Text(
                        text = stringResource(id = R.string.text_today_grow_title),
                        style = PetgrowTheme.typography.bold,
                        fontSize = 14.sp,
                        color = if (selectedTab == SelectTab.DAILYGROW) purple6C else black21
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
//                            selectedTab = SelectTab.TOTAL
                            onTotalClick()
                        }
                        .weight(1f)
                ) {
                    Icon(
                        painter = painterResource(
                            id = R.drawable.ic_total_select_tab
                        ),
                        tint = if (selectedTab == SelectTab.TOTAL) purple6C else black21, // 상태에 따른 색상 변경
                        contentDescription = "Collect",
                    )
                    Text(
                        text = stringResource(id = R.string.text_total_title),
                        fontSize = 14.sp,
                        style = PetgrowTheme.typography.medium,
                        color = if (selectedTab == SelectTab.TOTAL) purple6C else black21
                    )
                }
            }
        }
        Image(
            painter = painterResource(id = R.drawable.ic_add_tab),
            contentDescription = "Center Button",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -28.dp)
                .size(80.dp)
                .clickable { onSelectBottomClick() }
        )
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
    DAILYGROW, TOTAL, NAME
}


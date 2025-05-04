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
import kr.co.hyunwook.pet_grow_daily.feature.add.navigation.addNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.albumNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation.albumImageGraph
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.myPageNavigation
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.recordWriteGraph
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

    var navigatorEnum by remember { mutableStateOf(NavigateEnum.ALBUM) }


    LaunchedEffect(Unit) {

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
                            navigate(navigator, NavigateEnum.ALBUM)
                        },
                        navigateToOnBoarding = {
                            navigate(navigator)
                        }
                    )
                    albumNavGraph(
                        paddingValues = paddingValues,
                        navigateToAdd = {
                            navigate(navigator, NavigateEnum.ADD)
                        },
                        navigateToAlbumImage = {
                            navigate(navigator, NavigateEnum.ALBUM_IMAGE)
                        }

                    )
                    totalNavGraph()

                    myPageNavigation(
                        paddingValues = paddingValues
                    )

                    onboardingNavGraph(
                        navigateToName = {
//                            navigate(navigator, NavigateEnum.NAME)
                            navigate(navigator, NavigateEnum.ALBUM)

                        }
                    )
                    nameNavaGraph(
                        navigateToDailyGrow = {
//                            navigate(navigator, NavigateEnum.DAILYGROW)
                        }
                    )
                    addNavGraph(
                        navigateToRecordWrite = { selectedImageUris ->
                            navigator.navigateToRecordWrite(selectedImageUris)
                        }
                    )

                    albumImageGraph(
                        navigateToAlbum ={

                        }
                    )
                    recordWriteGraph(
                        navigateToAlbum = {
                            navigate(navigator, NavigateEnum.ALBUM)
                        }

                    )


                }
            }
        },
        bottomBar = {
            if (navigator.isShowBottomBar()) {
                CustomBottomBar(
                    onAlbumClick = {
                        navigatorEnum = NavigateEnum.ALBUM
                        navigate(navigator, NavigateEnum.ALBUM)
//                        coroutineScope.launch {
//                            sheetState.show()
//                        }
//                        isSheetOpen = true
                    },
                    onOrderClick = {
                        navigatorEnum = NavigateEnum.ORDER
                        navigate(navigator, NavigateEnum.ORDER)
                    },
                    onMyPageClick = {
                        navigatorEnum = NavigateEnum.MYPAGE
                        navigate(navigator, NavigateEnum.MYPAGE)
                    },
                    navigateEnum = navigatorEnum
                )

            }

        }
    )
}

fun navigate(navigator: MainNavigator, navigateEnum: NavigateEnum? = null) {
    val navOptions = navOptions {
        popUpTo(navigator.navController.graph.findStartDestination().id) {
            inclusive = true
        }
        launchSingleTop = true
    }
    when (navigateEnum) {
        NavigateEnum.ALBUM -> {
            navigator.navigateToAlbum(navOptions = navOptions)
        }
        NavigateEnum.ORDER -> {
            navigator.navigateToTotal(navOptions = navOptions)
        }
        NavigateEnum.MYPAGE -> {
            navigator.navigateToMyPage(navOptions = navOptions)
        }
        NavigateEnum.ADD -> {
            navigator.navigateToAdd(navOptions = navOptions)
        }
        NavigateEnum.ALBUM_IMAGE -> {
            navigator.navigateToAlbumImage(navOptions = navOptions)
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
    navigateEnum: NavigateEnum
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
                        tint = if (navigateEnum == NavigateEnum.ALBUM) purple6C else gray86,
                        contentDescription = "Album",
                    )
                    Text(
                        text = stringResource(id = R.string.text_album_title),
                        style = PetgrowTheme.typography.medium,
                        fontSize = 11.sp,
                        color = if (navigateEnum == NavigateEnum.ALBUM) purple6C else gray86
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
                        tint = if (navigateEnum == NavigateEnum.ORDER) purple6C else gray86, // 상태에 따른 색상 변경
                        contentDescription = "order",
                    )
                    Text(
                        text = stringResource(id = R.string.text_order_title),
                        fontSize = 11.sp,
                        style = PetgrowTheme.typography.medium,
                        color = if (navigateEnum == NavigateEnum.ORDER) purple6C else gray86
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
                        tint = if (navigateEnum == NavigateEnum.MYPAGE) purple6C else gray86, // 상태에 따른 색상 변경
                        contentDescription = "order",
                    )
                    Text(
                        text = stringResource(id = R.string.text_mypage_title),
                        fontSize = 11.sp,
                        style = PetgrowTheme.typography.medium,
                        color = if (navigateEnum == NavigateEnum.MYPAGE) purple6C else gray86
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

enum class NavigateEnum {
    ALBUM, ORDER, MYPAGE, ADD, RECORDWRITE, ALBUM_IMAGE
}


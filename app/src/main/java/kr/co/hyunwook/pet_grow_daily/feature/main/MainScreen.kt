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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.add.navigation.addNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.album.navigation.albumNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.anotherpet.navigation.anotherPetGraph
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.deliveryAddNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.deliveryCheckNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.deliveryListNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.delivery.navigation.deliveryRegisterNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.onboardingNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.profile.navigation.profileNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.main.splash.navigation.splashNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.alarmNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.businessInfoNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.myPageNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.albumLayoutNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.albumSelectNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.orderDoneNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.orderNavGraph
import kr.co.hyunwook.pet_grow_daily.feature.order.navigation.orderProductListGraph
import kr.co.hyunwook.pet_grow_daily.feature.recordwrite.navigation.recordWriteGraph
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.Alarm
import kr.co.hyunwook.pet_grow_daily.feature.mypage.navigation.BusinessInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
) {

    val orderViewModel: OrderViewModel = hiltViewModel()

    var navigatorEnum by remember { mutableStateOf(NavigateEnum.ALBUM) }


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
                            navigateToAlbum = {
                                navigatorEnum = NavigateEnum.ALBUM
                                navigate(navigator, NavigateEnum.ALBUM)
                            },
                            navigateToOnBoarding = {
                                navigate(navigator)
                            },
                            navigateToProfile = {
                                navigatorEnum = NavigateEnum.PROFILE
                                navigate(navigator, NavigateEnum.PROFILE)
                            }
                        )

                        albumNavGraph(
                            paddingValues = paddingValues,
                            navigateToAdd = {
                                navigate(navigator, NavigateEnum.ADD)
                            },
                            navigateToAnotherPet = {
                                navigatorEnum = NavigateEnum.ANOTHERPET
                                navigate(navigator, NavigateEnum.ANOTHERPET)
                            },
                            navigateToOrderProductList = {
                                navigatorEnum = NavigateEnum.ORDER_PRODUCT_LIST
                                navigate(navigator, NavigateEnum.ORDER_PRODUCT_LIST)
                            }
                        )
                        businessInfoNavGraph(
                            navigateToMyPage = { navigator.navController.popBackStack() }
                        )
                        alarmNavGraph(
                            navigateToMyPage = { navigator.navController.popBackStack() }
                        )
                        deliveryListNavGraph(
                            navigateToMyPage = {
                                navigator.navController.popBackStack()
                            },
                            navigateToDeliveryAdd = { deliveryId ->
                                navigator.navigateToDeliveryAdd(deliveryId)
                            }
                        )
                        myPageNavGraph(
                            navigateToDeliveryList = {
                                navigate(navigator, NavigateEnum.DELIVERY_LIST)
                            },
                            navigateToAlarm = {
                                navigate(navigator, NavigateEnum.ALARM)
                            },
                            navigateToBusinessInfo = {
                                navigate(navigator, NavigateEnum.BUSINESS_INFO)
                            }
                        )

                        profileNavGraph(
                            navigateToAlbum = {
                                navigate(navigator, NavigateEnum.ALBUM)
                            }
                        )

                        onboardingNavGraph(
                            navigateToAlbum = {
                                navigate(navigator, NavigateEnum.ALBUM)
                            },
                            navigateToProfile = {
                                navigate(navigator, NavigateEnum.PROFILE)
                            }
                        )

                        addNavGraph(
                            navigateToRecordWrite = { selectedImageUris ->
                                navigator.navigateToRecordWrite(selectedImageUris)
                            },
                            onBackClick = {
                                navigatorEnum = NavigateEnum.ALBUM
                                navigator.navController.popBackStack()
                            }
                        )

                        orderProductListGraph(
                            navigateToOrder = { orderProduct ->
                                navigatorEnum = NavigateEnum.ORDER
                                orderViewModel.addEvent(
                                    EventConstants.CLICK_ORDER_PRODUCT_TYPE_EVENT,
                                    mapOf(EventConstants.PRODUCT_TYPE_PROPERTY to orderProduct.productTitle)
                                )
                                navigator.navigateToOrder(orderProduct, navOptions {
                                    popUpTo(navigator.navController.graph.findStartDestination().id) {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                })
                            },
                            viewModel = orderViewModel
                        )

                        orderNavGraph(
                            navigateToAlbumSelect = {
                                navigate(navigator, NavigateEnum.ALBUM_SELECT)
                            },
                            onBackClick = {
                                navigatorEnum = NavigateEnum.ORDER_PRODUCT_LIST
                                navigator.navController.popBackStack()

                            },
                            viewModel = orderViewModel
                        )

                        orderDoneNavGraph(
                            navigateToAlbum = {
                                navigatorEnum = NavigateEnum.ALBUM
                                navigate(navigator, NavigateEnum.ALBUM)
                            }
                        )

                        albumLayoutNavGraph(
                            navigateToDeliveryCheck = {
                                navigatorEnum = NavigateEnum.DELIVERY_CHECK
                                navigate(navigator, NavigateEnum.DELIVERY_CHECK)
                            },
                            navigateToDeliveryRegister = {
                                navigatorEnum = NavigateEnum.DELIVERY_REGISTER
                                navigate(navigator, NavigateEnum.DELIVERY_REGISTER)
                            },
                            viewModel = orderViewModel,
                            onBackClick = {
                                orderViewModel.currentOrderProduct.value?.let { orderProduct ->
                                    navigator.navigateToOrder(orderProduct, navOptions {
                                        popUpTo(navigator.navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    })
                                }
                            }
                        )
                        albumSelectNavGraph(
                            navigateToAlbumLayout = {
                                navigatorEnum = NavigateEnum.ALBUM_LAYOUT
                                navigate(navigator, NavigateEnum.ALBUM_LAYOUT)
                            },
                            viewModel = orderViewModel,
                            onBackClick = {
                                orderViewModel.currentOrderProduct.value?.let { orderProduct ->
                                    navigator.navigateToOrder(orderProduct, navOptions {
                                        popUpTo(navigator.navController.graph.findStartDestination().id) {
                                            inclusive = true
                                        }
                                        launchSingleTop = true
                                    })
                                }
                            }
                        )

                        deliveryCheckNavGraph(
                            viewModel = orderViewModel,
                            navigateToOrderDone = {
                                navigatorEnum = NavigateEnum.ORDER_DONE
                                navigate(navigator, NavigateEnum.ORDER_DONE)
                            },
                            onBackClick = {
                                navigatorEnum = NavigateEnum.ALBUM_SELECT
                                navigate(navigator, NavigateEnum.ALBUM_SELECT)
                            }
                        )
                        deliveryRegisterNavGraph(
                            viewModel = orderViewModel,
                            navigateToOrderDone = {
                                navigatorEnum = NavigateEnum.ORDER_DONE
                                navigate(navigator, NavigateEnum.ORDER_DONE)

                            }
                        )

                        recordWriteGraph(
                            navigateToAlbum = {
                                navigate(navigator, NavigateEnum.ALBUM)
                            },
                            onBackClick = {
                                navigate(navigator, NavigateEnum.ADD)
                            }
                        )

                        anotherPetGraph()

                        deliveryAddNavGraph(
                            navigateToDeliveryList = {

                                navigator.navController.popBackStack()

//                                navigate(navigator, NavigateEnum.DELIVERY_LIST)
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
                        onOrderProductListClick = {
                            navigatorEnum = NavigateEnum.ORDER_PRODUCT_LIST
                            navigate(navigator, NavigateEnum.ORDER_PRODUCT_LIST)
                        },
                        onAnotherPetClick = {
                            navigatorEnum = NavigateEnum.ANOTHERPET
                            navigate(navigator, NavigateEnum.ANOTHERPET)
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

        NavigateEnum.ORDER_PRODUCT_LIST -> {
            navigator.navigateToOrderProductList(navOptions = navOptions)
        }

        NavigateEnum.ANOTHERPET -> {
            navigator.navigateToAnotherPet(navOptions = navOptions)
        }

        NavigateEnum.MYPAGE -> {
            navigator.navigateToMyPage(navOptions = navOptions)
        }

        NavigateEnum.ADD -> {
            navigator.navigateToAdd(navOptions = navOptions)
        }

        NavigateEnum.PROFILE -> {
            navigator.navigateToProfile(navOptions = navOptions)
        }

        NavigateEnum.DELIVERY_LIST -> {
            navigator.navigateToDeliveryList(navOptions = navOptions)
        }

        NavigateEnum.DELIVERY_ADD -> {
            navigator.navigateToDeliveryAdd(navOptions = navOptions)
        }

        NavigateEnum.DELIVERY_CHECK -> {
            navigator.navigateToDeliveryCheck(navOptions = navOptions)
        }

        NavigateEnum.DELIVERY_REGISTER -> {
            navigator.navigateToDeliveryRegister(navOptions = navOptions)
        }

        NavigateEnum.ALBUM_SELECT -> {
            navigator.navigateToAlbumSelect(navOptions = navOptions)
        }

        NavigateEnum.ALARM -> {
            navigator.navigateToAlarm(navOptions = navOptions)
        }
        NavigateEnum.ORDER_DONE -> {
            navigator.navigateToOrderDone(navOptions = navOptions)
        }
        NavigateEnum.BUSINESS_INFO -> {
            navigator.navigateToBusinessInfo(navOptions = navOptions)
        }
        NavigateEnum.ALBUM_LAYOUT -> {
            navigator.navigateToAlbumLayout(navOptions = navOptions)
        }
        else -> {
            navigator.navigateToOnBoarding(navOptions = navOptions)
        }
    }
}

@Composable
fun CustomBottomBar(
    onAlbumClick: () -> Unit,
    onOrderProductListClick: () -> Unit,
    onAnotherPetClick: () -> Unit,
    onMyPageClick: () -> Unit,
    navigateEnum: NavigateEnum
) {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(60.dp)
            .drawBehind {
                val strokeWidth = 0.5.dp.toPx()
                val borderColor = Color.Gray

                // 상단 테두리
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = strokeWidth
                )
                // 좌측 테두리
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
                // 우측 테두리
                drawLine(
                    color = borderColor,
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth
                )
            },
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
                        onOrderProductListClick()
                    }
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_order_tab
                    ),
                    tint = if (navigateEnum == NavigateEnum.ORDER_PRODUCT_LIST) purple6C else gray86, // 상태에 따른 색상 변경
                    contentDescription = "order",
                )
                Text(
                    text = stringResource(id = R.string.text_order_title),
                    fontSize = 11.sp,
                    style = PetgrowTheme.typography.medium,
                    color = if (navigateEnum == NavigateEnum.ORDER_PRODUCT_LIST) purple6C else gray86
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable {
                        onAnotherPetClick()
                    }
                    .weight(1f)
            ) {
                Icon(
                    painter = painterResource(
                        id = R.drawable.ic_anotherpet_tab
                    ),
                    tint = if (navigateEnum == NavigateEnum.ANOTHERPET) purple6C else gray86, // 상태에 따른 색상 변경
                    contentDescription = "order",
                )
                Text(
                    text = stringResource(id = R.string.text_another_title),
                    fontSize = 11.sp,
                    style = PetgrowTheme.typography.medium,
                    color = if (navigateEnum == NavigateEnum.ANOTHERPET) purple6C else gray86
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
    ALBUM, ORDER, ORDER_PRODUCT_LIST, ANOTHERPET, MYPAGE, ADD, RECORDWRITE,
    PROFILE, DELIVERY_ADD, DELIVERY_LIST, ALBUM_SELECT,
    DELIVERY_REGISTER, DELIVERY_CHECK, ALARM, ORDER_DONE, BUSINESS_INFO,
    ALBUM_LAYOUT
}

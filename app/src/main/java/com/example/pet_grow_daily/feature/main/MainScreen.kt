package com.example.pet_grow_daily.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.feature.home.navigation.homeNavGraph
import com.example.pet_grow_daily.feature.main.splash.navigation.splashNavGraph
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import kotlinx.coroutines.coroutineScope

@Composable
internal fun MainScreen(
    navigator: MainNavigator = rememberMainNavigator(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()

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
                        navigateToHome = {
                            val navOptions = navOptions {
                                popUpTo(navigator.navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                            navigator.navigateToHome(navOptions = navOptions)

                        }
                    )
                    homeNavGraph(
                        paddingValues = paddingValues
                    )
                }
            }
        },
        bottomBar = {
            CustomBottomBar()
        }
    )

}

@Composable
fun CustomBottomBar() {
    BottomAppBar(

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 첫 번째 아이템
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Add action for first item */ }) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_daily_grow),
                        contentDescription = "Today's Growth"
                    )
                    Text("오늘의 성장")
                }
            }

            // 두 번째 아이템
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {


                }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_total),
                        contentDescription = "Total View"
                    )
                    Text("토탈 뷰")
                }
            }

            // 세 번째 아이템
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Add action for third item */ }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_total),
                        contentDescription = "Profile"
                    )
                    Text("프로필")
                }
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
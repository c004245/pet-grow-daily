package com.example.pet_grow_daily.feature.main

import android.widget.Toast
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.feature.add.BottomSheetContent
import com.example.pet_grow_daily.feature.home.navigation.homeNavGraph
import com.example.pet_grow_daily.feature.main.splash.navigation.splashNavGraph
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    LaunchedEffect(Unit) {
        viewModel.saveDoneEvent.collect { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "저장이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                coroutineScope.launch {
                    sheetState.hide()
                }
                isSheetOpen = false
            } else {
                Toast.makeText(context, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
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
            CustomBottomBar(
                onTestClick = {

                    viewModel.getGrowRecord(getTodayDate())
                },
                onSelectBottomClick = {
                    coroutineScope.launch {
                        sheetState.show()
                    }
                    isSheetOpen = true
                }

            )
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


@Composable
fun CustomBottomBar(
    onTestClick: () -> Unit,
    onSelectBottomClick: () -> Unit
) {
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    onTestClick()

                }) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_daily_grow),
                        contentDescription = "Today's Growth"
                    )
                    Text("오늘의 성장")
                }
            }

            IconButton(
                modifier = Modifier.weight(1f),
                onClick =
                onSelectBottomClick
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_total),
                        contentDescription = "Total View"
                    )
                    Text("토탈 뷰")
                }
            }

            IconButton(
                modifier = Modifier.weight(1f),
                onClick = { }) {
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

fun getTodayDate(): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return sdf.format(Date())
}


@Preview
@Composable
fun MainScreenPreview() {
    PetgrowTheme {
        MainScreen()
    }
}


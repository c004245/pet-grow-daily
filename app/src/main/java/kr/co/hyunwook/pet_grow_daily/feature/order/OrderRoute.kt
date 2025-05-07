package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrderRoute(
    viewModel: OrderViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {

    }

    OrderScreen()
}

@Composable
fun 

package kr.co.hyunwook.pet_grow_daily.feature.anotherpet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnotherPetRoute(
    viewModel: AnotherPetViewModel = hiltViewModel()
) {

    val anotherImageList by viewModel.anotherPetList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAnotherPetList()
    }
    AnotherPetScreen()
}

@Composable
fun AnotherPetScreen() {
    Box(
        modifier = Modifier.fillMaxWidth()
    )
}

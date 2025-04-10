package kr.co.hyunwook.pet_grow_daily.feature.mypage

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyPageRoute(
    paddingValues: PaddingValues,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {

    }

    MyPageScreen(
        paddingValues = paddingValues,
    )
}

@Composable
fun MyPageScreen(
    paddingValues: PaddingValues,
) {

    Box(modifier = Modifier.fillMaxSize())   // Screen content will be placed here
}

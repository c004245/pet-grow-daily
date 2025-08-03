package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.EntryPointAccessors
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun OrderDoneRoute(
    navigateToAlbum: () -> Unit = {},
    viewModel: OrderDoneViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        // 화면이 표시될 때 주문 완료 알림 발송
        viewModel.sendOrderCompletionNotification()
    }

    OrderDoneScreen(
        navigateToAlbum = navigateToAlbum
    )
}

@Composable
fun OrderDoneScreen(
    navigateToAlbum: () -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_orderdone),
                contentDescription = null)
            Spacer(Modifier.height(20.dp))
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.text_order_done_title),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 18.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                textAlign = TextAlign.Center,
                text = stringResource(R.string.text_order_done_message),
                style = PetgrowTheme.typography.regular,
                color = gray86,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(32.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable {
                        navigateToAlbum()
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(purple6C)
            ) {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.text_home_navigate),
                    color = Color.White,
                    fontSize = 16.sp,
                    style = PetgrowTheme.typography.medium,
                    modifier = Modifier
                        .padding(top = 14.dp, bottom = 14.dp)
                        .align(Alignment.Center)
                )
            }

        }

    }

}
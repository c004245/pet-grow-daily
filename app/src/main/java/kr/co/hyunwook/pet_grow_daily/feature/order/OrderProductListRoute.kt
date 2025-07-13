package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


//주문 상품 고를 수 있는 화면
@Composable
fun OrderProductListRoute(
    navigateToOrder: () -> Unit,
    viewModel: OrderViewModel
) {

    OrderProductListScreen(
        navigateToOrder = navigateToOrder

    )
}

@Composable
fun OrderProductListScreen(
    navigateToOrder: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    )
}
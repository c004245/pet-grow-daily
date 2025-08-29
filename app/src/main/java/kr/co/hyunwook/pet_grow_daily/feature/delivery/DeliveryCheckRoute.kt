import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.flow.collectLatest
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryInfoItem
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.feature.order.PaymentWebView
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryCheckRoute(
    viewModel: OrderViewModel,
    navigateToOrderDone: () -> Unit,
    onBackClick: () -> Unit
) {
    val deliveryInfos by viewModel.deliveryInfos.collectAsState()
    val selectedAlbumRecords by viewModel.selectedAlbumRecords.collectAsState()
    val paymentData by viewModel.paymentData.collectAsState()
    val currentOrderProduct by viewModel.currentOrderProduct.collectAsState()
    var showPaymentWebView by remember { mutableStateOf(false) }
    var showProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val paymentResult by viewModel.paymentResult.collectAsState()

    var deliveryInfo by remember { mutableStateOf<DeliveryInfo?>(null) }

    Log.d("HWO", "선택된 앨범: ${selectedAlbumRecords.size}개")
    currentOrderProduct?.let { orderProduct ->
        Log.d("HWO", "전달받은 상품: ${orderProduct.productTitle}, ${orderProduct.productCost}")
    }

    Log.d("HWO", "DeliveryCheck state -> $showPaymentWebView --- $paymentData -- $showProcessing")

    LaunchedEffect(Unit) {
        viewModel.getDeliveryList()
        // 화면 진입 시 이전 결제 상태 초기화
        viewModel.clearAllPaymentStates()
    }

    LaunchedEffect(Unit) {
        viewModel.saveOrderDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                Log.d("HWO", "주문 저장 완료!")
                viewModel.addEvent(EventConstants.SHOW_ORDER_DONE_EVENT)
                // 주문 완료 후 모든 상태 초기화
                viewModel.clearAllPaymentStates()
                navigateToOrderDone()
            } else {
                Log.e("HWO", "주문 저장 실패")
                showProcessing = false
                // 실패 시에도 상태 초기화
                viewModel.clearPaymentResult()
            }
        }
    }

    LaunchedEffect(paymentData) {
        paymentData?.let { data ->
            Log.d("HWO", "결제 요청 데이터: $data")
            viewModel.addEvent(EventConstants.START_ORDER_PG_EVENT)
            showPaymentWebView = true
        }
    }

    LaunchedEffect(paymentResult) {
        when (val result = paymentResult) {
            is PaymentResult.Success -> {
                Log.d("HWO", "결제 성공 - 주문 저장 시작")
                showPaymentWebView = false
                showProcessing = true
                deliveryInfo?.let { deliveryInfos ->
                    viewModel.saveOrderRecord(deliveryInfos)
                }
            }

            is PaymentResult.Failure -> {
                Log.d("HWO", "결제 실패: ${result.message}")
                showPaymentWebView = false
                showProcessing = false
                // 결제 실패 시 상태 초기화
                viewModel.clearPaymentResult()
                Toast.makeText(context, "결제에 실패했습니다: ${result.message}", Toast.LENGTH_SHORT)
                    .show()
            }

            PaymentResult.Initial -> {
                // Initial 상태일 때는 아무것도 하지 않음
            }

            else -> {}
        }
    }

    when {
        showPaymentWebView && paymentData != null && currentOrderProduct != null -> {
            val currentPaymentData = paymentData
            val orderProduct = currentOrderProduct!!

            PaymentWebView(
                orderProduct = orderProduct,
                paymentData = currentPaymentData,
                onPaymentResult = { success, message, transactionId ->
                    Log.d(
                        "HWO",
                        "결제 결과 콜백 - success: $success, message: $message, transactionId: $transactionId"
                    )
                    if (success) {
                        val amount = currentPaymentData?.get("amount") ?: "0"
                        Log.d("HWO", "결제 성공 - imp_uid: $transactionId, amount: $amount")

                        viewModel.setPaymentResult(
                            PaymentResult.Success(
                                transactionId ?: "",
                                amount
                            )
                        )
                        Log.d("HWO", "결제 완료")
                    } else {
                        Log.d("HWO", "결제 실패 - message: $message, transactionId: $transactionId")
                        viewModel.setPaymentResult(PaymentResult.Failure(message ?: "결제 실패"))
                        Log.d("HWO", "결제 실패: $message")
                    }
                },
                onBackPressed = {
                    showPaymentWebView = false
                    viewModel.clearAllPaymentStates()
                }
            )
        }

        showProcessing -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.loading_animation)
                    )
                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 36.dp)
                            .height(200.dp)
                    )
                    Text(
                        text = "주문을 처리하고 있어요...",
                        style = PetgrowTheme.typography.bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        else -> {
            DeliveryCheckScreen(
                deliveryInfos = deliveryInfos,
                onOrderConfirm = { selectedDeliveryInfo ->
                    Log.d("HWO", "주문 확인 버튼 클릭 --> $")
                    viewModel.addEvent(EventConstants.CLICK_DELIVERY_DONE_EVENT)
                    deliveryInfo = selectedDeliveryInfo
                    currentOrderProduct?.let { orderProduct ->
                        Log.d("HWO", "주문 버튼 클릭 ")
                        viewModel.requestKakaoPayPayment(orderProduct)
                    }
                },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun DeliveryCheckScreen(
    deliveryInfos: List<DeliveryInfo>,
    onOrderConfirm: (DeliveryInfo) -> Unit,
    onBackClick: () -> Unit
) {

    val selectedDeliveryId = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(deliveryInfos) {
        if (selectedDeliveryId.value == null && deliveryInfos.isNotEmpty()) {
            selectedDeliveryId.value =
                deliveryInfos.find { it.isDefault }?.id ?: deliveryInfos.first().id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TitleDeliveryAppBarOnlyButton({
            onBackClick()
        })
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.text_delivery_check_title),
            style = PetgrowTheme.typography.medium,
            fontSize = 24.sp,
            color = black21
        )
        Spacer(Modifier.height(40.dp))
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(deliveryInfos.size) { index ->
                val deliveryInfo = deliveryInfos[index]
                DeliveryInfoItem(
                    deliveryInfo = deliveryInfo,
                    isSelected = selectedDeliveryId.value == deliveryInfo.id,
                    onItemClick = {
                        selectedDeliveryId.value = deliveryInfo.id
                    },
                    onEditClick = {
//                        onEditClick(deliveryInfo.id)
                    },
                    onDeleteClick = {
//                        onDeleteClick(deliveryInfo.id)
                    },
                    isDeleteShow = false
                )
                if (index < deliveryInfos.size - 1) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = grayDE
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (selectedDeliveryId.value != null) {
                        val selectedDeliveryInfo =
                            deliveryInfos.find { it.id == selectedDeliveryId.value }
                        if (selectedDeliveryInfo != null) {
                            onOrderConfirm(selectedDeliveryInfo)
                        }
                    }
                }
                .clip(RoundedCornerShape(14.dp))
                .background(purple6C)
        ) {
            Text(
                text = stringResource(R.string.text_confirm_done),
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

@Composable
fun TitleDeliveryAppBarOnlyButton(
    navigateToBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "ic_back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { navigateToBack() }
        )
    }
}
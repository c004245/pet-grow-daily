import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryInfoItem
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.feature.order.PaymentWebView
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import com.airbnb.lottie.compose.*

@Composable
fun DeliveryCheckRoute(
    viewModel: OrderViewModel,
    navigateToOrderDone: () -> Unit
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

    LaunchedEffect(Unit) {
        viewModel.getDeliveryList()
    }

    LaunchedEffect(Unit) {
        viewModel.saveOrderDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                Log.d("HWO", "주문 저장 완료!")
                navigateToOrderDone()
            } else {
                Log.e("HWO", "주문 저장 실패")
                showProcessing = false
            }
        }
    }

    LaunchedEffect(paymentData) {
        paymentData?.let { data ->
            Log.d("HWO", "결제 요청 데이터: $data")
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
                Toast.makeText(context, "결제에 실패했습니다: ${result.message}", Toast.LENGTH_SHORT)
                    .show()
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
                    if (success) {
                        val amount = currentPaymentData?.get("amount") ?: "0"
                        viewModel.setPaymentResult(
                            PaymentResult.Success(
                                transactionId ?: "",
                                amount
                            )
                        )
                        Log.d("HWO", "결제 완료")
                    } else {
                        viewModel.setPaymentResult(PaymentResult.Failure(message ?: "결제 실패"))
                        Log.d("HWO", "결제 실패: $message")
                    }
                },
                onBackPressed = {
                    showPaymentWebView = false
                    viewModel.clearPaymentRequest()
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
                selectedAlbumCount = selectedAlbumRecords.size,
                onOrderConfirm = { selectedDeliveryInfo ->
                    Log.d("HWO", "주문 확인 버튼 클릭")
                    deliveryInfo = selectedDeliveryInfo
                    currentOrderProduct?.let { orderProduct ->
                        viewModel.requestKakaoPayPayment(orderProduct)
                    }
                }
            )
        }
    }
}

@Composable
fun DeliveryCheckScreen(
    deliveryInfos: List<DeliveryInfo>,
    selectedAlbumCount: Int,
    onOrderConfirm: (DeliveryInfo) -> Unit
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
                    }
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
package kr.co.hyunwook.pet_grow_daily.feature.delivery

import TitleDeliveryAppBarOnlyButton
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.feature.order.PaymentWebView
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants

@Composable
fun DeliveryRegisterRoute(
    viewModel: OrderViewModel,
    navigateToOrderDone: () -> Unit,
    onBackClick: () -> Unit
) {

    val paymentData by viewModel.paymentData.collectAsState()
    val currentOrderProduct by viewModel.currentOrderProduct.collectAsState()
    var showPaymentWebView by remember { mutableStateOf(false) }
    var showProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val paymentResult by viewModel.paymentResult.collectAsState()
    var deliveryInfo by remember { mutableStateOf<DeliveryInfo?>(null) }

    currentOrderProduct?.let { orderProduct ->
        Log.d("HWO", "전달받은 상품: ${orderProduct.productTitle}, ${orderProduct.productDiscount}")
    }

    LaunchedEffect("saveOrderEvent") {
        viewModel.saveOrderDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                Log.d("HWO", "주문 저장 완료!")
                viewModel.addEvent(EventConstants.SHOW_ORDER_DONE_EVENT)
                navigateToOrderDone()
            } else {
                Log.e("HWO", "주문 저장 실패")
                showProcessing = false
            }
        }
    }

    LaunchedEffect("saveDeliveryEvent") {
        viewModel.saveDeliveryDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                Log.d("HWO", "saveDeliveryDone")
                viewModel.addEvent(EventConstants.CLICK_DELIVERY_DONE_EVENT)
                currentOrderProduct?.let { orderProduct ->
                    viewModel.requestKakaoPayPayment(orderProduct)
                }
            } else {
                Log.e("HWO", "배송지 저장 실패")
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
                deliveryInfo?.let { info ->
                    viewModel.saveOrderRecord(info)
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
            ProcessingScreen()
        }

        else -> {
            DeliveryRegisterScreen(
                onSaveClick = { info ->
                    deliveryInfo = info
                    viewModel.saveDeliveryInfo(info)
                },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun ProcessingScreen() {
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

@Composable
fun DeliveryRegisterScreen(
    onBackClick: () -> Unit,
    onSaveClick: (DeliveryInfo) -> Unit

) {

    var address by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showWebView by remember { mutableStateOf(false) }
    var isDefaultDelivery by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()


    val isFormValid = zipCode.isNotEmpty() && address.isNotEmpty() &&
            detailAddress.isNotEmpty() && recipientName.isNotEmpty() && phoneNumber.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(scrollState)
    ) {
        TitleDeliveryAppBarOnlyButton({
            onBackClick()
        })
        Spacer(Modifier.height(40.dp))
        Text(
            text = "앨범 받을 주소를\n입력해주세요.",
            style = PetgrowTheme.typography.medium,
            color = black21,
            fontSize = 24.sp
        )
        Spacer(Modifier.height(40.dp))
        AddressNumberWidget(
            zipCode = zipCode,
            onAddressSearchClick = {
                showWebView = true
            }
        )
        Spacer(Modifier.height(8.dp))
        AddressDetailWidget(
            address = address,
            detailAddress = detailAddress,
            onAddressChange = { address = it },
            onDetailAddressChange = { detailAddress = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = grayDE
        )

        Spacer(modifier = Modifier.height(16.dp))
        UserInfoDeliveryWidget(
            recipientName = recipientName,
            phoneNumber = phoneNumber,
            onRecipientNameChange = { recipientName = it },
            onPhoneNumberChange = { phoneNumber = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        CheckDefaultDeliveryWidget(
            isChecked = isDefaultDelivery,
            onCheckedChange = { isDefaultDelivery = it }
        )
        Spacer(Modifier.weight(1f))

        SaveButton(
            isEnabled = isFormValid,
            message = "완료",
            onSaveClick = {
                Log.d("HWO", "Save connect")
                val deliveryInfo = DeliveryInfo(
                    zipCode = zipCode,
                    address = address,
                    detailAddress = detailAddress,
                    name = recipientName,
                    phoneNumber = phoneNumber,
                    isDefault = isDefaultDelivery
                )
                onSaveClick(deliveryInfo)
            }
        )
    }
    if (showWebView) {
        PostcodeDialog(
            onDismiss = { showWebView = false },
            onAddressSelected = { selectedZipCode, selectedAddress ->
                zipCode = selectedZipCode
                address = selectedAddress
                showWebView = false
            }
        )
    }
}
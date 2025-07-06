package kr.co.hyunwook.pet_grow_daily.feature.delivery

import TitleDeliveryAppBarOnlyButton
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.feature.order.PaymentWebView
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryRegisterRoute(
    viewModel: OrderViewModel = hiltViewModel()
) {

    val paymentData by viewModel.paymentData.collectAsState()
    var showPaymentWebView by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val paymentResult by viewModel.paymentResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.saveDeliveryDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                Log.d("HWO", "saveDeliveryDone ")
                viewModel.requestKakaoPayPayment()
            }
        }
    }
    LaunchedEffect(paymentData) {
        paymentData?.let { data ->
            Log.d("HWO", "결제 요청 데이터: $data")
            showPaymentWebView = true
        }
    }


    if (showPaymentWebView) {
        PaymentWebView(
            onPaymentResult = { success, message, transactionId ->
                if (success) {
                    viewModel.setPaymentResult(PaymentResult.Success(transactionId ?: "", "27000"))
                    Log.d("HWO", "message done -> ")

                    Toast.makeText(context, "결제가 완료되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setPaymentResult(PaymentResult.Failure(message ?: "결제 실패"))
                    Log.d("HWO", "message error -> $message")
                    Toast.makeText(context, "결제에 실패했습니다: $message", Toast.LENGTH_SHORT).show()
                }
                showPaymentWebView = false
            },
            onBackPressed = {
                showPaymentWebView = false
            }
        )
    } else {
        DeliveryRegisterScreen(
            onSaveClick = { deliveryInfo ->
                viewModel.saveDeliveryInfo(deliveryInfo)

            },
            onBackClick = {

            }

        )
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
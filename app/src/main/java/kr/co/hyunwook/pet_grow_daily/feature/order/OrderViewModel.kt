package kr.co.hyunwook.pet_grow_daily.feature.order

import com.iamport.sdk.data.sdk.IamPortRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserAlbumCountUseCase
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getUserAlbumCountUseCase: GetUserAlbumCountUseCase
): ViewModel() {

    private val _userAlbumCount = MutableStateFlow<Int>(0)
    val userAlbumCount: StateFlow<Int> get() = _userAlbumCount

    private val _paymentRequest = MutableStateFlow<IamPortRequest?>(null)
    val paymentRequest: StateFlow<IamPortRequest?> = _paymentRequest

    private val _paymentResult = MutableStateFlow<PaymentResult?>(PaymentResult.Initial)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult


    fun getUserAlbumCount() {
        viewModelScope.launch {
            val userAlbumCount = getUserAlbumCountUseCase()
            Log.d("HWO", "userAlbumCount -> $userAlbumCount")
                _userAlbumCount.value = userAlbumCount
            }
        }

    fun requestPayment() {

        val request = IamPortRequest(
            pg = "inicis.INIpayTest",
            pay_method = "card",
            name = "코팅형 고급 앨범",
            merchant_uid = "order_${System.currentTimeMillis()}",
            amount = "27000",
            buyer_name = "구매자",
            buyer_tel = "010-1234-5678",
            buyer_email = "buyer@example.com",
            app_scheme = "petgrowdaily"
        )


        _paymentRequest.value = request
    }

    fun setPaymentResult(result: PaymentResult) {
        _paymentResult.value = result
    }

    fun clearPaymentRequest() {
        _paymentResult.value = null
    }
}





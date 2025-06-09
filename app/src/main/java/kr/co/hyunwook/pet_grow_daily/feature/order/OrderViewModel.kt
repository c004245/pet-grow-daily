package kr.co.hyunwook.pet_grow_daily.feature.order

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

    private val _paymentData = MutableStateFlow<Map<String, String>?>(null)
    val paymentData: StateFlow<Map<String, String>?> = _paymentData

    private val _paymentResult = MutableStateFlow<PaymentResult?>(PaymentResult.Initial)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult


    fun getUserAlbumCount() {
        viewModelScope.launch {
            val userAlbumCount = getUserAlbumCountUseCase()
            Log.d("HWO", "userAlbumCount -> $userAlbumCount")
                _userAlbumCount.value = userAlbumCount
            }
        }

    fun requestKakaoPayPayment() {
        val paymentData = mapOf(
            "userCode" to "imp26031685", // 제공받은 식별코드
            "pg" to "kakaopay.TC0ONETIME", // 카카오페이 테스트 PG 설정
            "pay_method" to "card",
            "name" to "코팅형 고급 앨범",
            "merchant_uid" to "album_${System.currentTimeMillis()}",
            "amount" to "27000",
            "buyer_name" to "구매자",
            "buyer_tel" to "010-1234-5678",
            "buyer_email" to "buyer@example.com",
            "app_scheme" to "petgrowdaily"
        )

        _paymentData.value = paymentData
    }

    fun setPaymentResult(result: PaymentResult) {
        _paymentResult.value = result
    }

    fun clearPaymentRequest() {
        _paymentData.value = null
    }
}

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAlbumRecordUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetDeliveryInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetDeliveryListUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.HasDeliveryInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveDeliveryInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveOrderRecordUseCase
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getUserAlbumCountUseCase: GetUserAlbumCountUseCase,
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase,
    private val hasDeliveryInfoUseCase: HasDeliveryInfoUseCase,
    private val getDeliveryListUseCase: GetDeliveryListUseCase,
    private val saveDeliveryInfoUseCase: SaveDeliveryInfoUseCase,
    private val saveOrderRecordUseCase: SaveOrderRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase

) : ViewModel() {

    private val _userAlbumCount = MutableStateFlow<Int>(0)
    val userAlbumCount: StateFlow<Int> get() = _userAlbumCount

    private val _paymentData = MutableStateFlow<Map<String, String>?>(null)
    val paymentData: StateFlow<Map<String, String>?> = _paymentData

    private val _paymentResult = MutableStateFlow<PaymentResult?>(PaymentResult.Initial)
    val paymentResult: StateFlow<PaymentResult?> = _paymentResult


    private val _albumRecord = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val albumRecord: StateFlow<List<AlbumRecord>> get() = _albumRecord

    private val _hasDeliveryInfo = MutableStateFlow<Boolean>(false)
    val hasDeliveryInfo: StateFlow<Boolean> get() = _hasDeliveryInfo

    private val _deliveryInfos = MutableStateFlow<List<DeliveryInfo>>(emptyList())
    val deliveryInfos: StateFlow<List<DeliveryInfo>> get() = _deliveryInfos


    private val _saveDeliveryDoneEvent = MutableSharedFlow<Boolean>()
    val saveDeliveryDoneEvent: SharedFlow<Boolean> get() = _saveDeliveryDoneEvent

    private val _selectedAlbumRecords = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val selectedAlbumRecords: StateFlow<List<AlbumRecord>> get() = _selectedAlbumRecords

    private val _saveOrderDoneEvent = MutableSharedFlow<Boolean>()
    val saveOrderDoneEvent: SharedFlow<Boolean> get() = _saveOrderDoneEvent


    fun checkDeliveryInfo() {
        viewModelScope.launch {
            hasDeliveryInfoUseCase().collect { hasInfo ->
                Log.d("HWO", "hasDelivery Info -> $hasInfo")

                _hasDeliveryInfo.value = hasInfo
            }
        }
    }

    fun getAlbumSelectRecord() {
        viewModelScope.launch {
            getAlbumRecordUseCase().collect { records ->
                _albumRecord.value = records
            }
        }
    }

    fun getDeliveryList() {
        viewModelScope.launch {
            getDeliveryListUseCase().collect {
                _deliveryInfos.value = it
            }
        }
    }
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

    fun saveDeliveryInfo(deliveryInfo: DeliveryInfo) {
        viewModelScope.launch {
            try {
                saveDeliveryInfoUseCase(deliveryInfo)
                _saveDeliveryDoneEvent.emit(true)
            } catch (e: Exception) {
                _saveDeliveryDoneEvent.emit(false)
            }
        }
    }

    fun clearPaymentRequest() {
        _paymentData.value = null
    }

    //선택한 앨범 리스트
    fun setSelectedAlbumRecords(records: List<AlbumRecord>) {
        _selectedAlbumRecords.value = records
    }

    fun saveOrderRecord(selectedDeliveryInfo: DeliveryInfo) {
        viewModelScope.launch {
            try {
                // 실제 사용자 ID 가져오기
                val userId = getUserIdUseCase.invoke()

                // 결제 정보 가져오기
                val paymentInfo = _paymentData.value ?: mapOf(
                    "merchant_uid" to "album_${System.currentTimeMillis()}",
                    "amount" to "27000",
                    "name" to "코팅형 고급 앨범"
                )

                Log.d("HWO", "주문 저장 시작:")
                Log.d("HWO", "- 사용자 ID: $userId")
                Log.d("HWO", "- 선택된 앨범: ${_selectedAlbumRecords.value.size}개")
                Log.d("HWO", "- 배송지: ${selectedDeliveryInfo.address}")
                Log.d("HWO", "- 결제 정보: $paymentInfo")

                val orderId = saveOrderRecordUseCase(
                    selectedAlbumRecords = _selectedAlbumRecords.value,
                    deliveryInfo = selectedDeliveryInfo,
                    paymentInfo = paymentInfo
                )

                Log.d("HWO", "주문 저장 완료 - OrderId: $orderId")

                // Firebase Function 호출하여 PDF 생성 요청
                callPdfGenerationFunction(orderId, userId)

                _saveOrderDoneEvent.emit(true)
            } catch (e: Exception) {
                _saveOrderDoneEvent.emit(false)
                Log.e("HWO", "주문 저장 실패: ${e.message}")
            }
        }
    }

    private fun callPdfGenerationFunction(orderId: String, userId: Long) {
        // 실제 Firebase Functions 호출 구현
        val functionData = hashMapOf(
            "orderId" to orderId,
            "userId" to userId.toString()
        )


        Firebase.functions
            .getHttpsCallable("generateAlbumPdf")
            .call(functionData)
            .addOnSuccessListener { result ->
                val data = result.data as Map<*, *>
                val pdfUrl = data["pdfUrl"] as? String
                val message = data["message"] as? String

                Log.d("HWO", "PDF 생성 성공: $pdfUrl")
                Log.d("HWO", "메시지: $message")
            }
            .addOnFailureListener { e ->
                Log.e("HWO", "PDF 생성 실패", e)
            }
    }
}

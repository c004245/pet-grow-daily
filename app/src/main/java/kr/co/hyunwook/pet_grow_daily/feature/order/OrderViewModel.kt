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
import com.google.firebase.functions.ktx.functions
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
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetTodayZipFileCountUseCase
import kr.co.hyunwook.pet_grow_daily.core.config.RemoteConfigWrapper
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import com.google.firebase.ktx.Firebase
import kr.co.hyunwook.pet_grow_daily.core.database.entity.SlackNotificationRequest
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.PostSlackUseCase

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getUserAlbumCountUseCase: GetUserAlbumCountUseCase,
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase,
    private val hasDeliveryInfoUseCase: HasDeliveryInfoUseCase,
    private val getDeliveryListUseCase: GetDeliveryListUseCase,
    private val saveDeliveryInfoUseCase: SaveDeliveryInfoUseCase,
    private val saveOrderRecordUseCase: SaveOrderRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val remoteConfigWrapper: RemoteConfigWrapper,
    private val getTodayZipFileCountUseCase: GetTodayZipFileCountUseCase,
    private val postSlackUseCase: PostSlackUseCase
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

    private val _orderProducts = MutableStateFlow<List<OrderProduct>>(emptyList())
    val orderProducts: StateFlow<List<OrderProduct>> get() = _orderProducts

    private val _todayZipFileCount = MutableStateFlow<Int>(0)
    val todayZipFileCount: StateFlow<Int> get() = _todayZipFileCount

    fun fetchOrderProducts() {
        remoteConfigWrapper.fetchOrderProductList { orderProductListModel ->
            if (orderProductListModel != null) {
                _orderProducts.value = orderProductListModel.orderProducts
            } else {
                // 기본값 사용

            }
        }
    }

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
                Log.d("HWO", "- 배송지: ${selectedDeliveryInfo.address} -- ${selectedDeliveryInfo.detailAddress}")
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
        val functionData = hashMapOf(
            "orderId" to orderId,
            "userId" to userId.toString()
        )

        Log.d("HWO", "Firebase Functions 호출 - orderId: $orderId, userId: $userId")

        Firebase.functions
            .getHttpsCallable("generateAlbumZipfile")
            .call(functionData)
            .addOnSuccessListener { result ->
                val data = result.data as Map<*, *>
                val zipUrl = data["zipUrl"] as? String
                val imageCount = data["imageCount"] as? Int
                val message = data["message"] as? String

                Log.d("HWO", "ZIP 파일 생성 성공: $zipUrl")
                Log.d("HWO", "이미지 개수: $imageCount")
                Log.d("HWO", "메시지: $message")

                // 슬랙 알림 전송
                sendSlackNotification(
                    orderId = orderId,
                    userId = userId,
                    zipUrl = zipUrl ?: "",
                    isSuccess = true
                )
            }
            .addOnFailureListener { e ->
                Log.e("HWO", "ZIP 파일 생성 실패", e)

                // 실패 시에도 슬랙 알림
                sendSlackNotification(
                    orderId = orderId,
                    userId = userId,
                    isSuccess = false,
                    errorMessage = e.message
                )
            }
    }

    private fun sendSlackNotification(
        orderId: String,
        userId: Long,
        zipUrl: String? = null,
        isSuccess: Boolean,
        errorMessage: String? = null
    ) {
        viewModelScope.launch {
            try {
                val request = SlackNotificationRequest(
                    orderId = orderId,
                    userId = userId,
                    zipUrl = zipUrl,
                    isSuccess = isSuccess,
                    errorMessage = errorMessage
                )

                postSlackUseCase(request)

            } catch (e: Exception) {
                Log.e("HWO", "슬랙 알림 전송 실패: ${e.message}")
            }
        }
    }

    fun getTodayOrderCount() {
        viewModelScope.launch {
            try {
                val count = getTodayZipFileCountUseCase()
                Log.d("HWO", "오늘 생성된 ZIP 파일 개수: $count")
                _todayZipFileCount.value = count
            } catch (e: Exception) {
                Log.e("HWO", "오늘 ZIP 파일 개수 조회 예외", e)
                _todayZipFileCount.value = 0
            }
        }
    }
}
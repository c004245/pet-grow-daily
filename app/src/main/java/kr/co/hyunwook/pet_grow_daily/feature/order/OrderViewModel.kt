package kr.co.hyunwook.pet_grow_daily.feature.order

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
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
import kr.co.hyunwook.pet_grow_daily.analytics.Analytics
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants
import kr.co.hyunwook.pet_grow_daily.core.database.entity.SlackNotificationRequest
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetFcmTokenUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.PostSlackUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveFcmTokenUseCase
import kr.co.hyunwook.pet_grow_daily.util.alarm.PhotoReminderNotificationManager
import kotlin.coroutines.resumeWithException

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase,
    private val hasDeliveryInfoUseCase: HasDeliveryInfoUseCase,
    private val getDeliveryListUseCase: GetDeliveryListUseCase,
    private val saveDeliveryInfoUseCase: SaveDeliveryInfoUseCase,
    private val saveOrderRecordUseCase: SaveOrderRecordUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val remoteConfigWrapper: RemoteConfigWrapper,
    private val getTodayZipFileCountUseCase: GetTodayZipFileCountUseCase,
    private val postSlackUseCase: PostSlackUseCase,
    private val getFcmTokenUseCase: GetFcmTokenUseCase,
    private val photoReminderNotificationManager: PhotoReminderNotificationManager,
    private val analytics: Analytics
) : ViewModel() {

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

    private val _selectedAlbumLayout = MutableStateFlow<AlbumLayoutType>(AlbumLayoutType.LAYOUT_A)
    val selectedAlbumLayout: StateFlow<AlbumLayoutType> get() = _selectedAlbumLayout

    private val _saveOrderDoneEvent = MutableSharedFlow<Boolean>()
    val saveOrderDoneEvent: SharedFlow<Boolean> get() = _saveOrderDoneEvent

    private val _orderProducts = MutableStateFlow<List<OrderProduct>>(emptyList())
    val orderProducts: StateFlow<List<OrderProduct>> get() = _orderProducts

    private val _isLoadingOrderProducts = MutableStateFlow<Boolean>(false)
    val isLoadingOrderProducts: StateFlow<Boolean> get() = _isLoadingOrderProducts

    private val _todayZipFileCount = MutableStateFlow<Int>(0)
    val todayZipFileCount: StateFlow<Int> get() = _todayZipFileCount

    private val _currentOrderProduct = MutableStateFlow<OrderProduct?>(null)
    val currentOrderProduct: StateFlow<OrderProduct?> get() = _currentOrderProduct

    // 결제 검증을 위한 imp_uid 저장
    private var currentImpUid: String? = null

    fun setCurrentOrderProduct(orderProduct: OrderProduct) {
        _currentOrderProduct.value = orderProduct
    }

    fun getCurrentOrderProduct(): OrderProduct? {
        return _currentOrderProduct.value
    }

    fun fetchOrderProducts() {
        _isLoadingOrderProducts.value = true
        remoteConfigWrapper.fetchOrderProductList { orderProductListModel ->
            if (orderProductListModel != null) {
                _orderProducts.value = orderProductListModel.orderProducts
            } else {
                // 기본값 사용

            }
            _isLoadingOrderProducts.value = false
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


    fun requestKakaoPayPayment(orderProduct: OrderProduct) {
        val discountedPrice = orderProduct.productCost * (100 - orderProduct.productDiscount) / 100

        val paymentData = mapOf(
            "channelKey" to "channel-key-0007adc4-c33d-471c-bd98-1ee0cc2fa7d5",
            "merchant_uid" to "order_" + System.currentTimeMillis(),
            "name" to orderProduct.productTitle,
            "amount" to "100",
            "m_redirect_url" to "https://pet-grow-daily.web.app/payment-result.html"
        )

        _paymentData.value = paymentData
    }

    fun setPaymentResult(result: PaymentResult) {
        _paymentResult.value = result
        // PaymentResult.Success에서 imp_uid 저장
        if (result is PaymentResult.Success) {
            currentImpUid = result.transactionId
        }
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



    fun clearPaymentResult() {
        _paymentResult.value = PaymentResult.Initial
    }

    fun clearAllPaymentStates() {
        _paymentData.value = null
        _paymentResult.value = PaymentResult.Initial
    }

    //선택한 앨범 리스트
    fun setSelectedAlbumRecords(records: List<AlbumRecord>) {
        _selectedAlbumRecords.value = records
    }

    fun setSelectedAlbumLayout(type: AlbumLayoutType) {
        _selectedAlbumLayout.value = type
    }
    fun saveOrderRecord(selectedDeliveryInfo: DeliveryInfo) {
        viewModelScope.launch {
            try {
                Log.d("HWO", "=== 주문 저장 프로세스 시작 ===")

                // 실제 사용자 ID 가져오기
                val userId = getUserIdUseCase.invoke()

                // 결제 정보 가져오기
                Log.d("HWO", "saveOrderRecord - paymentData: ${_paymentData.value}")
                val paymentInfo = _paymentData.value ?: throw IllegalStateException("결제 정보가 없습니다")

                Log.d("HWO", "주문 저장 시작:")
                Log.d("HWO", "- 사용자 ID: $userId")
                Log.d("HWO", "- 선택된 앨범: ${_selectedAlbumRecords.value.size}개")
                Log.d(
                    "HWO",
                    "- 배송지: ${selectedDeliveryInfo.address} -- ${selectedDeliveryInfo.detailAddress}"
                )
                Log.d("HWO", "- 결제 정보: $paymentInfo")
                Log.d("HWO", "- 현재 저장된 imp_uid: $currentImpUid")

                // 결제 서버 검증을 paymentInfo에서 필요한 값만 넘겨서 검증하도록 변경
                val verificationParams = mapOf(
                    "impUid" to (currentImpUid ?: ""),
                    "merchantUid" to (paymentInfo["merchant_uid"] ?: ""),
                    "expectedAmount" to (paymentInfo["amount"] ?: ""),
                    "userId" to userId.toString()
                )

                Log.d("HWO", "서버 검증 요청 파라미터: $verificationParams")

                val verifyResult = verifyPaymentOnServer(verificationParams)

                if (!verifyResult) {
                    Log.e("HWO", "서버 결제 검증 실패")
                    Log.e("HWO", "검증 실패한 결제 정보: $paymentInfo")
                    Log.e("HWO", "검증 실패한 imp_uid: $currentImpUid")
                    _saveOrderDoneEvent.emit(false)
                    return@launch
                }

                Log.d("HWO", "서버 결제 검증 성공 - 주문 생성 진행")

                val fcmToken = getFcmTokenUseCase.invoke().first()
                Log.d("HWO", "FCM 토큰 획득: ${fcmToken?.take(10)}...")

                val orderId = saveOrderRecordUseCase(
                    selectedAlbumRecords = _selectedAlbumRecords.value,
                    selectedAlbumLayoutType = _selectedAlbumLayout.value,
                    deliveryInfo = selectedDeliveryInfo,
                    paymentInfo = paymentInfo,
                    fcmToken = fcmToken ?: ""
                )

                Log.d("HWO", "주문 저장 완료 - OrderId: $orderId")

                val productName = paymentInfo["name"] ?: ""
                val productAmount = paymentInfo["amount"]?.toIntOrNull() ?: 0
                addEvent(
                    EventConstants.DONE_ORDER_PG_EVENT,
                    mapOf(
                        EventConstants.PRODUCT_TITLE_PROPERTY to productName,
                        EventConstants.PRODUCT_DISCOUNT_PROPERTY to productAmount
                    )
                )

                Log.d("HWO", "ZIP 파일 생성 요청 시작")
                // Firebase Function 호출하여 ZIP 생성 요청 (결과를 기다림)
                callPdfGenerationFunction(orderId, userId)

                Log.d("HWO", "=== 주문 저장 프로세스 완료 ===")
                // ZIP 생성이 완료된 후에만 완료 이벤트 발생
                _saveOrderDoneEvent.emit(true)
            } catch (e: Exception) {
                Log.e("HWO", "=== 주문 저장 프로세스 실패 ===")
                Log.e("HWO", "실패 원인: ${e.message}", e)
                _saveOrderDoneEvent.emit(false)
            }
        }
    }

    private suspend fun callPdfGenerationFunction(orderId: String, userId: Long) {
        return suspendCancellableCoroutine { continuation ->
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
                    viewModelScope.launch {
                        sendSlackNotification(
                            orderId = orderId,
                            userId = userId,
                            zipUrl = zipUrl ?: "",
                            isSuccess = true
                        )
                    }

                    addEvent(EventConstants.DONE_CREATE_ZIP_FILE_EVENT)
                    // 성공적으로 완료
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    Log.e("HWO", "ZIP 파일 생성 실패", e)

                    // 실패 시에도 슬랙 알림
                    viewModelScope.launch {
                        sendSlackNotification(
                            orderId = orderId,
                            userId = userId,
                            isSuccess = false,
                            errorMessage = e.message
                        )
                    }

                    // 실패로 완료
                    continuation.resumeWithException(e)
                }
        }
    }

    /**
     * 결제 정보 서버 검증 (Firebase Functions와 연동)
     */
    private suspend fun verifyPaymentOnServer(paymentInfo: Map<String, String>): Boolean {
        return suspendCancellableCoroutine { continuation ->
            Log.d("HWO", "=== 서버 결제 검증 시작 ===")

            val functionData = hashMapOf<String, Any>()
            paymentInfo.forEach { (key, value) ->
                functionData[key] = value
            }

            Log.d("HWO", "Firebase Functions 호출 준비:")
            Log.d("HWO", "- 함수명: verifyPayment")
            Log.d("HWO", "- 전달 데이터: $functionData")

            Firebase.functions
                .getHttpsCallable("verifyPayment")
                .call(functionData)
                .addOnSuccessListener { result ->
                    Log.d("HWO", "Firebase Functions 호출 성공")

                    val data = result.data as? Map<*, *>
                    Log.d("HWO", "서버 응답 데이터: $data")

                    val success = data?.get("success") as? Boolean ?: false
                    val verified = data?.get("verified") as? Boolean ?: false
                    val message = data?.get("message") as? String
                    val error = data?.get("error") as? String
                    val refundProcessed = data?.get("refundProcessed") as? Boolean ?: false

                    Log.d("HWO", "서버 검증 결과 분석:")
                    Log.d("HWO", "- success: $success")
                    Log.d("HWO", "- verified: $verified")
                    Log.d("HWO", "- message: $message")
                    Log.d("HWO", "- error: $error")
                    Log.d("HWO", "- refundProcessed: $refundProcessed")

                    val finalResult = success && verified

                    if (finalResult) {
                        Log.d("HWO", "=== 서버 결제 검증 성공 ===")
                    } else {
                        Log.e("HWO", "=== 서버 결제 검증 실패 ===")
                        Log.e("HWO", "실패 사유: $error")

                        if (refundProcessed) {
                            Log.w("HWO", "자동 환불 처리 완료")
                            // 사용자에게 환불 완료 메시지 표시
                            viewModelScope.launch {
                                // Toast나 Dialog로 사용자에게 알림
                                Log.i("HWO", "사용자 알림: 결제 검증 실패로 자동 환불 처리되었습니다.")
                            }
                        } else {
                            Log.e("HWO", "환불 처리 실패 - 고객센터 문의 필요")
                            viewModelScope.launch {
                                // 고객센터 문의 안내
                                Log.e("HWO", "사용자 알림: 결제 처리 중 문제가 발생했습니다. 고객센터에 문의해주세요.")
                            }
                        }
                    }

                    continuation.resume(finalResult)
                }
                .addOnFailureListener { e ->
                    Log.e("HWO", "=== Firebase Functions 호출 실패 ===")
                    Log.e("HWO", "오류 유형: ${e.javaClass.simpleName}")
                    Log.e("HWO", "오류 메시지: ${e.message}")
                    Log.e("HWO", "오류 상세:", e)
                    Log.e("HWO", "네트워크 오류로 인한 검증 실패 - 고객센터 문의 필요")
                    continuation.resume(false)
                }
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

    fun scheduleOrderAvailableNotification() {
        photoReminderNotificationManager.scheduleOrderAvailableNotification()
    }

    fun addEvent(event: String, properties: Map<String, Any>? = null) {
        Log.d("HWO", "addEvent -> $event -- $properties")
        when (event) {
            EventConstants.CLICK_ORDER_PRODUCT_TYPE_EVENT -> {
                analytics.track(event, properties)
            }

            EventConstants.CLICK_ORDER_START_EVENT -> {
                analytics.track(event)
            }

            EventConstants.CLICK_ALBUM_SELECT_DONE_EVENT -> {
                analytics.track(event)
            }

            EventConstants.CLICK_ALBUM_LAYOUT_DONE_EVENT -> {
                analytics.track(event, properties)
            }

            EventConstants.CLICK_DELIVERY_DONE_EVENT -> {
                analytics.track(event)
            }

            EventConstants.START_ORDER_PG_EVENT -> {
                analytics.track(event)
            }

            EventConstants.DONE_ORDER_PG_EVENT -> {
                analytics.track(event, properties)
            }

            EventConstants.DONE_CREATE_ZIP_FILE_EVENT -> {
                analytics.track(event)
            }

            EventConstants.SHOW_ORDER_DONE_EVENT -> {
                analytics.track(event)
            }
        }
    }
}
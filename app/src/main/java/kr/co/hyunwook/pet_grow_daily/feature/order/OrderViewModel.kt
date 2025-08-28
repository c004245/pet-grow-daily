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
import com.google.firebase.messaging.FirebaseMessaging
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
            "merchant_uid" to "test_" + System.currentTimeMillis(),
            "name" to orderProduct.productTitle,
            "amount" to "100",
            "m_redirect_url" to "https://pet-grow-daily.web.app/payment-result.html"
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

    fun setSelectedAlbumLayout(type: AlbumLayoutType) {
        _selectedAlbumLayout.value = type
    }
    fun saveOrderRecord(selectedDeliveryInfo: DeliveryInfo) {
        viewModelScope.launch {
            try {
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

                val fcmToken = getFcmTokenUseCase.invoke().first()
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
                // Firebase Function 호출하여 ZIP 생성 요청 (결과를 기다림)
                callPdfGenerationFunction(orderId, userId)

                // ZIP 생성이 완료된 후에만 완료 이벤트 발생
                _saveOrderDoneEvent.emit(true)
            } catch (e: Exception) {
                _saveOrderDoneEvent.emit(false)
                Log.e("HWO", "주문 저장 실패: ${e.message}")
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
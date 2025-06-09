package kr.co.hyunwook.pet_grow_daily.core.model

sealed class PaymentResult {
    object Initial : PaymentResult()
    data class Success(val transactionId: String, val amount: String) : PaymentResult()
    data class Failure(val message: String) : PaymentResult()
}

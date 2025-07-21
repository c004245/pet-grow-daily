package kr.co.hyunwook.pet_grow_daily.core.database.entity

data class PaymentData(
    val pg: String,
    val pay_method: String,
    val name: String,
    val merchant_uid: String,
    val amount: String,
    val app_scheme: String
)
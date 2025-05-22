package kr.co.hyunwook.pet_grow_daily.core.model
import com.iamport.sdk.data.sdk.IamPortResponse
import com.iamport.sdk.domain.core.Iamport

sealed class PaymentResult {
    object Initial : PaymentResult()
    data class Success(val response: IamPortResponse) : PaymentResult()
    data class Failure(val message: String) : PaymentResult()
}

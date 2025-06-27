package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import javax.inject.Inject

class DeleteDeliveryInfoUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(id: Int) = deliveryRepository.deleteDeliveryInfo(id)
}
package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import javax.inject.Inject

class SaveDeliveryInfoUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(deliveryInfo: DeliveryInfo) =
        deliveryRepository.insertDeliveryInfo(deliveryInfo)
}
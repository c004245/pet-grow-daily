package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import javax.inject.Inject

class GetDeliveryInfoUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(id: Int): Flow<DeliveryInfo> =
        deliveryRepository.getDeliveryInfoById(id)

}
package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import javax.inject.Inject

class HasDeliveryInfoUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(): Flow<Boolean> =
        deliveryRepository.hasDeliveryInfo()
}
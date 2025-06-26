package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery.DeliveryRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery.navigation.DeliveryList
import javax.inject.Inject

class GetDeliveryListUseCase @Inject constructor(
    private val deliveryRepository: DeliveryRepository
) {
    suspend operator fun invoke(): Flow<List<DeliveryInfo>> =
        deliveryRepository.getDeliveryList()

}
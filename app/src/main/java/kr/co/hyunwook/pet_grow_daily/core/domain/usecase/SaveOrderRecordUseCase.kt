package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumLayoutType
import javax.inject.Inject

class SaveOrderRecordUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(
        orderId: String,
        selectedAlbumRecords: List<AlbumRecord>,
        selectedAlbumLayoutType: AlbumLayoutType,
        deliveryInfo: DeliveryInfo,
        paymentInfo: Map<String, String>,
        fcmToken: String,
    ): String =
        albumRepository.saveOrderRecord(
            orderId,
            selectedAlbumRecords,
            selectedAlbumLayoutType,
            deliveryInfo,
            paymentInfo,
            fcmToken
        )
}

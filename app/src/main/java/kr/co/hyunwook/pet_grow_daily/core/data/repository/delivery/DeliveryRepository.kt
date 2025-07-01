package kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo

interface DeliveryRepository {
    suspend fun insertDeliveryInfo(deliveryInfo: DeliveryInfo)

    suspend fun getDeliveryList(): Flow<List<DeliveryInfo>>

    suspend fun deleteDeliveryInfo(id: Int)

    suspend fun getDeliveryInfoById(id: Int): Flow<DeliveryInfo>

    suspend fun hasDeliveryInfo(): Flow<Boolean>

}
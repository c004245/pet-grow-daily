package kr.co.hyunwook.pet_grow_daily.core.data.repository.delivery

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.DeliveryInfoDao
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import javax.inject.Inject

class DeliveryRepositoryImpl @Inject constructor(
    private val deliveryInfoDao: DeliveryInfoDao,
) : DeliveryRepository {

    override suspend fun insertDeliveryInfo(deliveryInfo: DeliveryInfo) {
        if (deliveryInfo.isDefault) {
            deliveryInfoDao.clearAllDefaultDelivery()
        }
        deliveryInfoDao.insertDeliveryInfo(deliveryInfo)
    }

    override suspend fun getDeliveryList(): Flow<List<DeliveryInfo>> {
        return deliveryInfoDao.getDeliveryList()
    }

    override suspend fun deleteDeliveryInfo(id: Int) {
        deliveryInfoDao.deleteDeliveryInfo(id)
    }

    override suspend fun getDeliveryInfoById(id: Int): Flow<DeliveryInfo> {
        return deliveryInfoDao.getDeliveryInfoById(id)
    }

}

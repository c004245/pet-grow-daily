package kr.co.hyunwook.pet_grow_daily.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo

@Dao
interface DeliveryInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliveryInfo(info: DeliveryInfo)


    @Query("SELECT * FROM DeliveryInfo ORDER BY id DESC")
    fun getDeliveryList(): Flow<List<DeliveryInfo>>

    @Query("DELETE FROM DeliveryInfo WHERE id = :id")
    suspend fun deleteDeliveryInfo(id: Int)

    @Query("SELECT * FROM deliveryinfo WHERE id = :id")
    fun getDeliveryInfoById(id: Int): Flow<DeliveryInfo>

    @Query("UPDATE DeliveryInfo SET isDefault = 0 WHERE isDefault = 1")
    suspend fun clearAllDefaultDelivery()

    @Query("SELECT EXISTS(SELECT 1 FROM DeliveryInfo LIMIT 1)")
    fun hasDeliveryInfo(): Flow<Boolean>
}

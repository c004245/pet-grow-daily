package kr.co.hyunwook.pet_grow_daily.core.database

import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import androidx.room.Database
import androidx.room.RoomDatabase
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile

@Database(
    entities = [
        AlbumRecord::class,
        DeliveryInfo::class,
        PetProfile::class,
    ],
    version = 4,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun albumRecordDao(): AlbumRecordDao
    abstract fun deliveryInfoDao(): DeliveryInfoDao
}
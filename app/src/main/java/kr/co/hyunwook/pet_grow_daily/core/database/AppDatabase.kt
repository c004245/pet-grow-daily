package kr.co.hyunwook.pet_grow_daily.core.database

import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import androidx.room.Database
import androidx.room.RoomDatabase
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord

@Database(
    entities = [AlbumRecord::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun albumRecordDao(): AlbumRecordDao
}
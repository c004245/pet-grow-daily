package com.example.pet_grow_daily.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pet_grow_daily.core.database.entity.GrowRecord

@Database(
    entities = [GrowRecord::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun growRecordDao(): GrowRecordDao
}
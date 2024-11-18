package com.example.pet_grow_daily.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pet_grow_daily.core.database.entity.GrowRecord

@Dao
interface GrowRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowRecord(record: GrowRecord)

    @Query("SELECT * FROM GrowRecord")
    fun getGrowRecord(): List<GrowRecord>
}
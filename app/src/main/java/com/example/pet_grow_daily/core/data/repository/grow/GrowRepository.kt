package com.example.pet_grow_daily.core.data.repository.grow

import androidx.annotation.WorkerThread
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import kotlinx.coroutines.flow.Flow

interface GrowRepository {
    suspend fun saveGrowRecord(growRecord: GrowRecord)

    suspend fun getTodayGrowRecord(todayDate: String): Flow<List<GrowRecord>>

    suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>>

    suspend fun saveName(name: String)
}
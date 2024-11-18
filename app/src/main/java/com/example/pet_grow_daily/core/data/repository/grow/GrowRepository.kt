package com.example.pet_grow_daily.core.data.repository.grow

import androidx.annotation.WorkerThread
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import kotlinx.coroutines.flow.Flow

interface GrowRepository {
    suspend fun saveRecord(growRecord: GrowRecord)
}
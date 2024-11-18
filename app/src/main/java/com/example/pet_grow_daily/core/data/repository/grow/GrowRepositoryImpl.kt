package com.example.pet_grow_daily.core.data.repository.grow

import androidx.annotation.VisibleForTesting
import androidx.annotation.WorkerThread
import com.example.pet_grow_daily.core.database.GrowRecordDao
import com.example.pet_grow_daily.core.database.entity.GrowRecord

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GrowRepositoryImpl @Inject constructor(
    private val growRecordDao: GrowRecordDao
): GrowRepository {


    override suspend fun saveRecord(growRecord: GrowRecord) {

    }
}
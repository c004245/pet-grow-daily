package com.example.pet_grow_daily.core.domain.usecase

import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayGrowRecordUseCase @Inject constructor(
    private val growRepository: GrowRepository
) {
    suspend operator fun invoke(todayDate: String): Flow<List<GrowRecord>> =
        growRepository.getTodayGrowRecord(todayDate)
}
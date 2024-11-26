package com.example.pet_grow_daily.core.domain.usecase

import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyCategoryGrowRecordsUseCase @Inject constructor(
    private val growRepository: GrowRepository
) {
    suspend operator fun invoke(categoryType: CategoryType, month: String): Flow<List<GrowRecord>> =
        growRepository.getMonthlyCategoryGrowRecords(categoryType, month)
}
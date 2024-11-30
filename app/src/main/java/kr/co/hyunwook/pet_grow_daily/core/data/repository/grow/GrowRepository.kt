package kr.co.hyunwook.pet_grow_daily.core.data.repository.grow

import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow

interface GrowRepository {
    suspend fun saveGrowRecord(growRecord: GrowRecord)

    suspend fun getTodayGrowRecord(todayDate: String): Flow<List<GrowRecord>>

    suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>>

    suspend fun getMonthlyCategoryGrowRecords(categoryType: CategoryType, month: String): Flow<List<GrowRecord>>
    suspend fun saveName(name: String)

    suspend fun getName(): Flow<String>
}
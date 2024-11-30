package kr.co.hyunwook.pet_grow_daily.core.data.repository.grow

import kr.co.hyunwook.pet_grow_daily.core.database.GrowRecordDao
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.GrowPreferencesDataSource
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GrowRepositoryImpl @Inject constructor(
    private val growRecordDao: GrowRecordDao,
    private val growDataSource: GrowPreferencesDataSource
): GrowRepository {

    override suspend fun saveGrowRecord(growRecord: GrowRecord) {
        growRecordDao.insertGrowRecord(growRecord)
    }

    override suspend fun getTodayGrowRecord(todayDate: String): Flow<List<GrowRecord>> {
        return growRecordDao.getTodayGrowRecord(todayDate)
    }

    override suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>> {
        return growRecordDao.getMonthlyGrowRecords(month)
    }

    override suspend fun getMonthlyCategoryGrowRecords(
        categoryType: CategoryType,
        month: String
    ): Flow<List<GrowRecord>> {
        return growRecordDao.getMonthlyCategoryGrowRecords(categoryType, month)
    }

    override suspend fun saveName(name: String) {
        growDataSource.saveName(name)
    }

    override suspend fun getName(): Flow<String> = growDataSource.name


}
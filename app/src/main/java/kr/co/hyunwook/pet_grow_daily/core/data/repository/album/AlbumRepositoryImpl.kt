package kr.co.hyunwook.pet_grow_daily.core.data.repository.album

import kr.co.hyunwook.pet_grow_daily.core.database.AlbumRecordDao
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumRecordDao: AlbumRecordDao,
    private val growDataSource: AlbumPreferencesDataSource
): AlbumRepository {

    override suspend fun insertAlbumRecord(albumRecord: AlbumRecord) {
        albumRecordDao.insertAlbumRecord(albumRecord)
    }

    override suspend fun getAlbumRecord(): Flow<List<AlbumRecord>> {
     return albumRecordDao.getAlbumRecord()
    }
//    }(todayDate: String): Flow<List<GrowRecord>> {
//        return growRecordDao.getTodayGrowRecord(todayDate)
//    }

//    override suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>> {
//        return albumRecordDao.getMonthlyGrowRecords(month)
//    }
//
//    override suspend fun getMonthlyCategoryGrowRecords(
//        categoryType: CategoryType,
//        month: String
//    ): Flow<List<GrowRecord>> {
//        return growRecordDao.getMonthlyCategoryGrowRecords(categoryType, month)
//    }

    override suspend fun saveName(name: String) {
        growDataSource.saveName(name)
    }

    override suspend fun getName(): Flow<String> = growDataSource.name


}
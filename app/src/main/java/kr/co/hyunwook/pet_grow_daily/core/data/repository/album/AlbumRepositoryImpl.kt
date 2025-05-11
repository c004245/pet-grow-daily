package kr.co.hyunwook.pet_grow_daily.core.data.repository.album

import kr.co.hyunwook.pet_grow_daily.core.database.AlbumRecordDao
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation.AlbumImage
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumRecordDao: AlbumRecordDao,
    private val albumDataSource: AlbumPreferencesDataSource
): AlbumRepository {

    override suspend fun insertAlbumRecord(albumRecord: AlbumRecord) {
        albumRecordDao.insertAlbumRecord(albumRecord)
    }

    override suspend fun getAlbumRecord(): Flow<List<AlbumRecord>> {
     return albumRecordDao.getAlbumRecord()
    }

    override suspend fun getAllImageAsList(): Flow<List<AlbumImageModel>> {
        return albumRecordDao.getAllImageAsList()
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


    override suspend fun saveLoginState(userId: Long) {
        albumDataSource.saveLoginState(userId)

    }
    override suspend fun getHasCompleteOnBoarding(): Flow<Boolean> = albumDataSource.hasCompletedOnboarding




}
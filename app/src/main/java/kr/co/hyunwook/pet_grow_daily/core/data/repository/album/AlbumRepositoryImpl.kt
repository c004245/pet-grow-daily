package kr.co.hyunwook.pet_grow_daily.core.data.repository.album

import com.google.firebase.firestore.FirebaseFirestore
import kr.co.hyunwook.pet_grow_daily.core.database.AlbumRecordDao
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.FirestoreAlbumDataSource
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumRecordDao: AlbumRecordDao,
    private val albumDataSource: AlbumPreferencesDataSource,
    private val firestoreDataSource: FirestoreAlbumDataSource
) : AlbumRepository {

    override suspend fun hasPetProfile(): Flow<Boolean> {
        val userId = getUserId()
        return firestoreDataSource.hasPetProfile(userId)
    }
    override suspend fun savePetProfile(profile: PetProfile) {
        albumRecordDao.savePetProfile(profile)

        val userId = getUserId()
        firestoreDataSource.savePetProfile(profile, userId)
    }

    override suspend fun saveOrderRecord(
        selectedAlbumRecords: List<AlbumRecord>,
        deliveryInfo: DeliveryInfo,
        paymentInfo: Map<String, String>,

    ): String {

        val userId = getUserId()
        return firestoreDataSource.saveOrderRecord(
            selectedAlbumRecords,
            deliveryInfo,
            paymentInfo,
            userId
        )
    }
    override suspend fun getPetProfile(): Flow<PetProfile?> {
        return albumRecordDao.getPetProfile()
    }
    override suspend fun insertAlbumRecord(albumRecord: AlbumRecord) {
        albumRecordDao.insertAlbumRecord(albumRecord)

        if (albumRecord.isPublic) {
            val userId = getUserId()
            firestoreDataSource.saveAlbumRecord(albumRecord, userId)
        }
    }


    override suspend fun getUserAlbumCount(): Int {
        val userId = getUserId()
        return firestoreDataSource.getUserAlbumCount(userId)
    }


    override suspend fun getAnotherPetAlbums(): Flow<List<AnotherPetModel>> {
        return firestoreDataSource.getAnotherPetAlbums()
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

    private suspend fun getUserId(): Long {
        return try {
            albumDataSource.getUserId() ?: 0L // 유저 ID가 없으면 일단 0으로 설정
        } catch (e: Exception) {
            // 사용자 ID를 가져오지 못하면 기본값 0 반환
            e.printStackTrace()
            0L
        }
    }


    override suspend fun saveLoginState(userId: Long) {
        albumDataSource.saveLoginState(userId)

    }

    override suspend fun getHasCompleteOnBoarding(): Flow<Boolean> =
        albumDataSource.hasCompletedOnboarding




}

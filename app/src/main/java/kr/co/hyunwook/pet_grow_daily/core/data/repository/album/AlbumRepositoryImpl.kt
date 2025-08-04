package kr.co.hyunwook.pet_grow_daily.core.data.repository.album

import com.google.firebase.firestore.FirebaseFirestore
import kr.co.hyunwook.pet_grow_daily.core.database.AlbumRecordDao
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.FirestoreAlbumDataSource
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.google.firebase.firestore.DocumentSnapshot

class AlbumRepositoryImpl @Inject constructor(
    private val albumRecordDao: AlbumRecordDao,
    private val albumDataSource: AlbumPreferencesDataSource,
    private val firestoreDataSource: FirestoreAlbumDataSource
) : AlbumRepository {

    override suspend fun saveFcmToken(fcmToken: String) {
        albumDataSource.saveFcmToken(fcmToken)
    }
    override suspend fun hasPetProfile(): Flow<Boolean> {
        return flow {
            val userId = getUserId()
            firestoreDataSource.hasPetProfile(userId).collect {
                emit(it)
            }
        }
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
        fcmToken: String
    ): String {
        val userId = getUserId()
        return firestoreDataSource.saveOrderRecord(
            selectedAlbumRecords,
            deliveryInfo,
            paymentInfo,
            userId,
            fcmToken
        )
    }

    override suspend fun getPetProfile(): Flow<PetProfile?> {
        return albumRecordDao.getPetProfile()
    }

    override suspend fun insertAlbumRecord(albumRecord: AlbumRecord) {
        albumRecordDao.insertAlbumRecord(albumRecord)

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        albumDataSource.updateLastPhotoDate(today)

        if (albumRecord.isPublic) {
            val userId = getUserId()
            firestoreDataSource.saveAlbumRecord(albumRecord, userId)
        }
    }

    override suspend fun getUserAlbumCount(): Int {
        val userId = getUserId()
        return firestoreDataSource.getUserAlbumCount(userId)
    }


    override suspend fun shouldDisableUploadButton(): Boolean {
        return albumRecordDao.shouldDisableUploadButton()
    }

    override suspend fun getAnotherPetAlbumsWithPaging(
        pageSize: Int,
        lastDocument: DocumentSnapshot?
    ): Pair<List<AnotherPetModel>, DocumentSnapshot?> {
        return firestoreDataSource.getAnotherPetAlbumsWithPaging(pageSize, lastDocument)
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

//    override fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>> {
//        return albumRecordDao.getMonthlyGrowRecords(month)
//    }
//
//    override fun getMonthlyCategoryGrowRecords(
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


    override suspend fun getFcmToken(): Flow<String?> {
        return albumDataSource.fcmToken
    }


    override suspend fun saveLoginState(userId: Long, nickName: String?, email: String?) {
        albumDataSource.saveLoginState(userId, nickName, email)
    }

    override suspend fun getHasCompleteOnBoarding(): Flow<Boolean> =
        albumDataSource.hasCompletedOnboarding

    override suspend fun getTodayZipFileCount(): Int {
        return firestoreDataSource.getTodayZipFileCount()
    }

    override suspend fun setPhotoReminderEnabled(enabled: Boolean) {
        albumDataSource.setPhotoReminderEnabled(enabled)
    }

    override suspend fun isPhotoReminderEnabled(): Flow<Boolean> {
        return albumDataSource.isPhotoReminderEnabled()
    }

    override suspend fun setDeliveryNotificationEnabled(enabled: Boolean) {
        albumDataSource.setDeliveryNotificationEnabled(enabled)
    }

    override suspend fun isDeliveryNotificationEnabled(): Flow<Boolean> {
        return albumDataSource.isDeliveryNotificationEnabled()
    }

    override suspend fun setMarketingNotificationEnabled(enabled: Boolean) {
        albumDataSource.setMarketingNotificationEnabled(enabled)
    }

    override suspend fun isMarketingNotificationEnabled(): Flow<Boolean> {
        return albumDataSource.isMarketingNotificationEnabled()
    }

    override suspend fun updateLastPhotoDate(date: String) {
        albumDataSource.updateLastPhotoDate(date)
    }

    override suspend fun getLastPhotoDate(): Flow<String?> {
        return albumDataSource.getLastPhotoDate()
    }

    override suspend fun getTodayUserPhotoCount(): Int {
        return firestoreDataSource.getTodayUserPhotoCount()
    }
}

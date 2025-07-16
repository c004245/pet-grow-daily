package kr.co.hyunwook.pet_grow_daily.core.data.repository.album


import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile

interface AlbumRepository {
    suspend fun insertAlbumRecord(albumRecord: AlbumRecord)

    suspend fun saveOrderRecord(
        selectedAlbumRecords: List<AlbumRecord>,
        deliveryInfo: DeliveryInfo,
        paymentInfo: Map<String, String>
    ): String

    suspend fun savePetProfile(profile: PetProfile)

    suspend fun getUserAlbumCount(): Int

    suspend fun hasPetProfile(): Flow<Boolean>

    suspend fun getPetProfile(): Flow<PetProfile?>

    suspend fun getAnotherPetAlbums(): Flow<List<AnotherPetModel>>

    suspend fun getAlbumRecord(): Flow<List<AlbumRecord>>

    suspend fun getAllImageAsList(): Flow<List<AlbumImageModel>>

//    suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>>

//    suspend fun getMonthlyCategoryGrowRecords(categoryType: CategoryType, month: String): Flow<List<GrowRecord>>

    suspend fun getHasCompleteOnBoarding(): Flow<Boolean>

    suspend fun saveLoginState(userId: Long, nickName: String?, email: String?)

    suspend fun getTodayZipFileCount(): Int

    suspend fun setPhotoReminderEnabled(enabled: Boolean)

    suspend fun isPhotoReminderEnabled(): Flow<Boolean>

    suspend fun setDeliveryNotificationEnabled(enabled: Boolean)

    suspend fun isDeliveryNotificationEnabled(): Flow<Boolean>

    suspend fun setMarketingNotificationEnabled(enabled: Boolean)

    suspend fun isMarketingNotificationEnabled(): Flow<Boolean>

    suspend fun updateLastPhotoDate(date: String)

    suspend fun getLastPhotoDate(): Flow<String?>
}

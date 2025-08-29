package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import com.google.firebase.firestore.DocumentSnapshot
import kr.co.hyunwook.pet_grow_daily.feature.order.AlbumLayoutType

interface FirestoreAlbumDataSource {
    suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long)

    suspend fun saveOrderRecord(
        selectedAlbumRecords: List<AlbumRecord>,
        selectedAlbumLayoutType: AlbumLayoutType,
        deliveryInfo: DeliveryInfo,
        paymentInfo: Map<String, String>,
        userId: Long,
        fcmToken: String
    ): String

    suspend fun getUserAlbumCount(userId: Long): Int

    suspend fun getAnotherPetAlbumsWithPaging(
        pageSize: Int = 30,
        lastDocument: DocumentSnapshot? = null
    ): Pair<List<AnotherPetModel>, DocumentSnapshot?>

    suspend fun savePetProfile(profile: PetProfile, userId: Long)

    suspend fun hasPetProfile(userId: Long): Flow<Boolean>

    suspend fun getTodayZipFileCount(): Int

    suspend fun getTodayUserPhotoCount(): Int
}

const val ALBUM_CREATE_COMPLETE = 42
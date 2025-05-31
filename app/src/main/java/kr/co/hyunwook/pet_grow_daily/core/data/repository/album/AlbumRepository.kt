package kr.co.hyunwook.pet_grow_daily.core.data.repository.album


import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile

interface AlbumRepository {
    suspend fun insertAlbumRecord(albumRecord: AlbumRecord)

    suspend fun savePetProfile(profile: PetProfile)

    suspend fun getUserAlbumCount(): Int

    suspend fun hasPetProfile(): Flow<Boolean>
    suspend fun getAnotherPetAlbums(): Flow<List<AnotherPetModel>>


    suspend fun getAlbumRecord(): Flow<List<AlbumRecord>>

    suspend fun getAllImageAsList(): Flow<List<AlbumImageModel>>

//    suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>>

//    suspend fun getMonthlyCategoryGrowRecords(categoryType: CategoryType, month: String): Flow<List<GrowRecord>>

    suspend fun getHasCompleteOnBoarding(): Flow<Boolean>

    suspend fun saveLoginState(userId: Long)
}
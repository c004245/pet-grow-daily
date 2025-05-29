package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile

interface FirestoreAlbumDataSource {
    suspend fun saveAlbumRecord(record: AlbumRecord, userId: Long)

    suspend fun getUserAlbumCount(userId: Long): Int

    suspend fun getAnotherPetAlbums(): Flow<List<AnotherPetModel>>

    suspend fun savePetProfile(profile: PetProfile, userId: Long)
}


const val ALBUM_CREATE_COMPLETE = 40
package kr.co.hyunwook.pet_grow_daily.core.data.repository.album

import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord

interface AlbumRepository {
    suspend fun insertAlbumRecord(albumRecord: AlbumRecord)

    suspend fun getAlbumRecord(): Flow<List<AlbumRecord>>

//    suspend fun getMonthlyGrowRecord(month: String): Flow<List<GrowRecord>>

//    suspend fun getMonthlyCategoryGrowRecords(categoryType: CategoryType, month: String): Flow<List<GrowRecord>>
    suspend fun saveName(name: String)

    suspend fun getName(): Flow<String>
}
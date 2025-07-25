package kr.co.hyunwook.pet_grow_daily.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile

@Dao
interface AlbumRecordDao {

    //저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumRecord(record: AlbumRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePetProfile(petProfile: PetProfile)

    //앨범 데이터 불러오기
    @Query("SELECT * FROM AlbumRecord ORDER BY date DESC")
    fun getAlbumRecord(): Flow<List<AlbumRecord>>

    @Query("""
        SELECT firstImage AS imageUrl, date From AlbumRecord WHERE firstImage != ''
        UNION ALL
        SELECT secondImage AS imageUrl, date From AlbumRecord WHERE secondImage != '' 
        ORDER BY date DESC
    """)
    fun getAllImageAsList(): Flow<List<AlbumImageModel>>

    @Query("SELECT * FROM PetProfile LIMIT 1")
    fun getPetProfile(): Flow<PetProfile?>


}

package kr.co.hyunwook.pet_grow_daily.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo

@Dao
interface AlbumRecordDao {

    //저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbumRecord(record: AlbumRecord)

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
//    @Query(""" SELECT * FROM GrowRecord  WHERE strftime('%m', DATE(timeStamp / 1000, 'unixepoch', 'localtime')) = :month""")
//    fun getMonthlyGrowRecords(month: String): Flow<List<GrowRecord>>
//
//    //카테고리에 해당되는 월 데이터 출력
//    @Query("""SELECT * FROM GrowRecord  WHERE strftime('%m', timeStamp / 1000, 'unixepoch', 'localtime') = :month AND categoryType = :categoryType""")
//    fun getMonthlyCategoryGrowRecords(
//        categoryType: CategoryType,
//        month: String
//    ): Flow<List<GrowRecord>>


    @Query("SELECT * FROM DeliveryInfo ORDER BY id DESC")
    fun getDeliveryList(): Flow<List<DeliveryInfo>>
}
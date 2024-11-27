package com.example.pet_grow_daily.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowRecordDao {

    //저장
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGrowRecord(record: GrowRecord)

    //오늘 데이터 불러오기
    @Query("SELECT * FROM GrowRecord WHERE DATE(timeStamp / 1000, 'unixepoch', 'localtime') = :date")
    fun getTodayGrowRecord(date: String): Flow<List<GrowRecord>>

    @Query(""" SELECT * FROM GrowRecord  WHERE strftime('%m', DATE(timeStamp / 1000, 'unixepoch', 'localtime')) = :month""")
    fun getMonthlyGrowRecords(month: String): Flow<List<GrowRecord>>

    //카테고리에 해당되는 월 데이터 출력
    @Query("""SELECT * FROM GrowRecord  WHERE strftime('%m', timeStamp / 1000, 'unixepoch', 'localtime') = :month AND categoryType = :categoryType""")
    fun getMonthlyCategoryGrowRecords(
        categoryType: CategoryType,
        month: String
    ): Flow<List<GrowRecord>>

}
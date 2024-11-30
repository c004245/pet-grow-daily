package kr.co.hyunwook.pet_grow_daily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kr.co.hyunwook.pet_grow_daily.feature.add.EmotionType

@Entity
data class GrowRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photoUrl: String,
    val categoryType: CategoryType,
    val emotionType: EmotionType,
    val memo: String,
    val timeStamp: Long
)
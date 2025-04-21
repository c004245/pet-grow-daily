package kr.co.hyunwook.pet_grow_daily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date : Long,
    val content: String,
    val firstImage: String,
    val secondImage: String
)

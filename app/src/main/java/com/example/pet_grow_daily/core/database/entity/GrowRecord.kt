package com.example.pet_grow_daily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GrowRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val photoUrl: String,
    val categoryType: String,
    val emotionType: String,
    val memo: String,
    val timeStamp: Long
)
package kr.co.hyunwook.pet_grow_daily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeliveryInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val zipCode: String,
    val address: String,
    val detailAddress: String,
    val name: String,
    val phoneNumber: String,
    val isDefault: Boolean = false
)

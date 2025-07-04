package kr.co.hyunwook.pet_grow_daily.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumRecord(
    @PrimaryKey(autoGenerate = true) val date: Long,
    val content: String,
    val firstImage: String,
    val secondImage: String,
    val isPublic: Boolean, //공유하겠다.
    val isFinal: Boolean = false  //결제완료
)

data class AlbumImageModel(
    val imageUrl: String,
    val date: Long
)

data class AnotherPetModel(
    val firstImage: String,
    val secondImage: String,
    val content: String
)

@Entity
data class PetProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val profileImageUrl: String,
)
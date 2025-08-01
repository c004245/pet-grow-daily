package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import com.google.firebase.firestore.DocumentSnapshot
import javax.inject.Inject

class GetAnotherPetImageUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(
        pageSize: Int = 30,
        lastDocument: DocumentSnapshot? = null
    ): Pair<List<AnotherPetModel>, DocumentSnapshot?> {
        return  albumRepository.getAnotherPetAlbumsWithPaging(pageSize, lastDocument)
    }
}

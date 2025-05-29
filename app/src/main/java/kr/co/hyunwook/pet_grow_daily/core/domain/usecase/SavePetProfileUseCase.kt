package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import javax.inject.Inject

class SavePetProfileUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(petProfile: PetProfile) =
        albumRepository.savePetProfile(petProfile)
}

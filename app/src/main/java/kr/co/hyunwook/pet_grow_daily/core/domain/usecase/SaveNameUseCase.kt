package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val growRepository: AlbumRepository
) {
    suspend operator fun invoke(name: String) = growRepository.saveName(name)

}
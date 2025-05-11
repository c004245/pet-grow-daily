package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import javax.inject.Inject

class SaveLoginStateUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(userId: Long) =
        albumRepository.saveLoginState(userId)
}

package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNameUseCase @Inject constructor(
    private val growRepository: AlbumRepository
) {
    suspend operator fun invoke(): Flow<String> =
        growRepository.getName()
}
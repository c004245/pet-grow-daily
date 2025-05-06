package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kotlinx.coroutines.flow.Flow
import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import javax.inject.Inject

class GetAllImageUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke(): Flow<List<AlbumImageModel>> =
        albumRepository.getAllImageAsList()
}

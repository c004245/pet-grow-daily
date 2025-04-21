package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import javax.inject.Inject

class SaveAlbumRecordUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
){
    suspend operator fun invoke(albumRecord: AlbumRecord) =
        albumRepository.insertAlbumRecord(albumRecord)

}
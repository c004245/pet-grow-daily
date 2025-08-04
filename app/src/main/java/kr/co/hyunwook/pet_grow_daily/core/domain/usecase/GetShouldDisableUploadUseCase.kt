package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import javax.inject.Inject

/**
 * 오늘 2번 업로드를 넘겼을 경우 사진 추가 버튼 막기 UseCase
 */
class GetShouldDisableUploadUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke() = albumRepository.shouldDisableUploadButton()
}
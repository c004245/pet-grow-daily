package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 선택한 달 데이터 가져오기
 */
class GetMonthlyGrowRecordUseCase @Inject constructor(
    private val growRepository: AlbumRepository
) {
//    suspend operator fun invoke(month: String): Flow<List<GrowRecord>> =
//        growRepository.getMonthlyGrowRecord(month)

}
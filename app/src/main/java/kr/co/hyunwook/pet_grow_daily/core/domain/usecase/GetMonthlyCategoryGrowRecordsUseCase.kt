package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyCategoryGrowRecordsUseCase @Inject constructor(
    private val growRepository: AlbumRepository
) {
//    suspend operator fun invoke(categoryType: CategoryType, month: String): Flow<List<GrowRecord>> =
//        growRepository.getMonthlyCategoryGrowRecords(categoryType, month)
}
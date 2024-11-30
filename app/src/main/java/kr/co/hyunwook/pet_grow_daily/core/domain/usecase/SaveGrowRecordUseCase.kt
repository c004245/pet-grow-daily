package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.grow.GrowRepository
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import javax.inject.Inject

class SaveGrowRecordUseCase @Inject constructor(
    private val growRepository: GrowRepository
){
    suspend operator fun invoke(growRecord: GrowRecord) =
        growRepository.saveGrowRecord(growRecord)

}
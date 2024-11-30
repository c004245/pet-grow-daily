package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.data.repository.grow.GrowRepository
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val growRepository: GrowRepository
) {
    suspend operator fun invoke(name: String) = growRepository.saveName(name)

}
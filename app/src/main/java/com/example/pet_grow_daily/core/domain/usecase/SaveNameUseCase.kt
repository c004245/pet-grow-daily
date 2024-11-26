package com.example.pet_grow_daily.core.domain.usecase

import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import javax.inject.Inject

class SaveNameUseCase @Inject constructor(
    private val growRepository: GrowRepository
) {
    suspend operator fun invoke(name: String) = growRepository.saveName(name)

}
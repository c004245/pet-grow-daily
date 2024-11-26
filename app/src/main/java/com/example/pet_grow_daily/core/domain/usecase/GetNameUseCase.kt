package com.example.pet_grow_daily.core.domain.usecase

import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetNameUseCase @Inject constructor(
    private val growRepository: GrowRepository
) {
    suspend operator fun invoke(): Flow<String> =
        growRepository.getName()
}
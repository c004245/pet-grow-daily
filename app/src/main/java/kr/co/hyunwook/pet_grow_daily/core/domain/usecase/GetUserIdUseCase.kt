package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import javax.inject.Inject

class GetUserIdUseCase @Inject constructor(
    private val albumPreferencesDataSource: AlbumPreferencesDataSource
) {
    suspend operator fun invoke(): Long {
        return albumPreferencesDataSource.getUserId() ?: 0L

    }
}
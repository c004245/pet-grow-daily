package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import javax.inject.Inject

class GetUserInfoUseCase @Inject constructor(
    private val albumPreferencesDataSource: AlbumPreferencesDataSource
) {
    suspend operator fun invoke(): Pair<String, String> {
        return Pair(
            albumPreferencesDataSource.getNickName() ?: "견주",
            albumPreferencesDataSource.getEmail() ?: "DailyDog"
        )
    }
}
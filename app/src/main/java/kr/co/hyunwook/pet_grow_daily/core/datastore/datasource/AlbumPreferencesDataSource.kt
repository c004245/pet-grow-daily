package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface AlbumPreferencesDataSource {
    val hasCompletedOnboarding: Flow<Boolean>
    suspend fun saveLoginState(userId: Long, nickName: String?, email: String?)
     suspend fun getUserId(): Long?
    suspend fun getEmail(): String?
    suspend fun getNickName(): String?
}
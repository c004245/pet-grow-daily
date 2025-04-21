package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface AlbumPreferencesDataSource {
    val name: Flow<String>

    suspend fun saveName(name: String)
}
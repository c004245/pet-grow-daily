package com.example.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface GrowPreferencesDataSource {
    val name: Flow<String>

    suspend fun saveName(name: String)
}
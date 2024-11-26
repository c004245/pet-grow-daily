package com.example.pet_grow_daily.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class DefaultGrowPreferencesDataSource @Inject constructor(
    @Named("grow") private val dataStore: DataStore<Preferences>
): GrowPreferencesDataSource {

    object PreferencesKey {
        val NAME = stringPreferencesKey("NAME")
    }

    override val name = dataStore.data.map { preferences ->
        preferences[PreferencesKey.NAME] ?: ""
    }

    override suspend fun saveName(name: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.NAME] = name
        }
    }
}

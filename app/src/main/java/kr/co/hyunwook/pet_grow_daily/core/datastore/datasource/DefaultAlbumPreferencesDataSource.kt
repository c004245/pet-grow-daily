package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named

class DefaultAlbumPreferencesDataSource @Inject constructor(
    @Named("album") private val dataStore: DataStore<Preferences>
): AlbumPreferencesDataSource {

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

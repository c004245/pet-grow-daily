package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Named
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

class DefaultAlbumPreferencesDataSource @Inject constructor(
    @Named("album") private val dataStore: DataStore<Preferences>
): AlbumPreferencesDataSource {

    override suspend fun getUserId(): Long? {

        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.USER_ID]
        }.firstOrNull()
    }

    object PreferencesKey {
        val USER_ID = longPreferencesKey("USER_ID")
        val hasCompletedOnBoarding = booleanPreferencesKey("HAS_COMPLETED_ONBOARDING")
    }

    override val hasCompletedOnboarding = dataStore.data.map { preferences ->
        preferences[PreferencesKey.hasCompletedOnBoarding] ?: false
    }


    override suspend fun saveLoginState(userId: Long) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_ID] = userId
            preferences[PreferencesKey.hasCompletedOnBoarding] = true
        }
    }
}

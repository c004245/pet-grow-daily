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
import androidx.datastore.preferences.core.preferencesOf
import android.util.Log

class DefaultAlbumPreferencesDataSource @Inject constructor(
    @Named("album") private val dataStore: DataStore<Preferences>
): AlbumPreferencesDataSource {

    override suspend fun getUserId(): Long? {

        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.USER_ID]
        }.firstOrNull()
    }

    override suspend fun getNickName(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.NICKNAME]
        }.firstOrNull()
    }

    override suspend fun getEmail(): String? {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.EMAIL]
        }.firstOrNull()
    }

    object PreferencesKey {
        val USER_ID = longPreferencesKey("USER_ID")
        val NICKNAME = stringPreferencesKey("NICK_NAME")
        val EMAIL = stringPreferencesKey("EMAIL")
        val hasCompletedOnBoarding = booleanPreferencesKey("HAS_COMPLETED_ONBOARDING")

        // 알림 설정 관련 키
        val PHOTO_REMINDER_ENABLED = booleanPreferencesKey("PHOTO_REMINDER_ENABLED")
        val DELIVERY_NOTIFICATION_ENABLED = booleanPreferencesKey("DELIVERY_NOTIFICATION_ENABLED")
        val MARKETING_NOTIFICATION_ENABLED = booleanPreferencesKey("MARKETING_NOTIFICATION_ENABLED")
        val LAST_PHOTO_DATE = stringPreferencesKey("LAST_PHOTO_DATE")
    }

    override val hasCompletedOnboarding = dataStore.data.map { preferences ->
        preferences[PreferencesKey.hasCompletedOnBoarding] ?: false
    }


    override suspend fun saveLoginState(userId: Long, nickName: String?, email: String?) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.USER_ID] = userId
            preferences[PreferencesKey.NICKNAME] = nickName ?: "견주"
            preferences[PreferencesKey.EMAIL] = email ?: "dailydog@gmail.com"
            preferences[PreferencesKey.hasCompletedOnBoarding] = true
        }
    }

    // 알림 설정 관련 메서드들
    override suspend fun setPhotoReminderEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.PHOTO_REMINDER_ENABLED] = enabled
        }
        Log.d("DefaultAlbumPreferencesDataSource", "사진 리마인더 설정 저장: $enabled")
    }

    override suspend fun isPhotoReminderEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.PHOTO_REMINDER_ENABLED] ?: true
        }
    }

    override suspend fun setDeliveryNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.DELIVERY_NOTIFICATION_ENABLED] = enabled
        }
        Log.d("DefaultAlbumPreferencesDataSource", "배송 알림 설정 저장: $enabled")
    }

    override suspend fun isDeliveryNotificationEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.DELIVERY_NOTIFICATION_ENABLED] ?: true
        }
    }

    override suspend fun setMarketingNotificationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.MARKETING_NOTIFICATION_ENABLED] = enabled
        }
        Log.d("DefaultAlbumPreferencesDataSource", "마케팅 알림 설정 저장: $enabled")
    }

    override suspend fun isMarketingNotificationEnabled(): Flow<Boolean> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.MARKETING_NOTIFICATION_ENABLED] ?: true
        }
    }

    override suspend fun updateLastPhotoDate(date: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKey.LAST_PHOTO_DATE] = date
        }
        Log.d("DefaultAlbumPreferencesDataSource", "마지막 사진 등록 날짜 저장: $date")
    }

    override suspend fun getLastPhotoDate(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[PreferencesKey.LAST_PHOTO_DATE]
        }
    }
}

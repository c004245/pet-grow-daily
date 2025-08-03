package kr.co.hyunwook.pet_grow_daily.core.datastore.datasource

import kotlinx.coroutines.flow.Flow

interface AlbumPreferencesDataSource {
    val hasCompletedOnboarding: Flow<Boolean>
    suspend fun saveLoginState(userId: Long, nickName: String?, email: String?)
    suspend fun getUserId(): Long?
    suspend fun getEmail(): String?
    suspend fun getNickName(): String?
    val fcmToken: Flow<String?>


    // 알림 설정 관련 메서드들
    suspend fun setPhotoReminderEnabled(enabled: Boolean)
    suspend fun isPhotoReminderEnabled(): Flow<Boolean>
    suspend fun setDeliveryNotificationEnabled(enabled: Boolean)
    suspend fun isDeliveryNotificationEnabled(): Flow<Boolean>
    suspend fun setMarketingNotificationEnabled(enabled: Boolean)
    suspend fun isMarketingNotificationEnabled(): Flow<Boolean>
    suspend fun updateLastPhotoDate(date: String)
    suspend fun saveFcmToken(fcmToken: String)
    suspend fun getLastPhotoDate(): Flow<String?>
}
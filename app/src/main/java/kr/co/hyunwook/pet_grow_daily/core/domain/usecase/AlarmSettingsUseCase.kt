package kr.co.hyunwook.pet_grow_daily.core.domain.usecase

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kr.co.hyunwook.pet_grow_daily.core.data.repository.album.AlbumRepository
import kr.co.hyunwook.pet_grow_daily.core.datastore.datasource.AlbumPreferencesDataSource
import javax.inject.Inject

class AlarmSettingsUseCase @Inject constructor(
    private val albumRepository: AlbumRepository
) {

    // 사진 리마인더 설정
    suspend fun setPhotoReminderEnabled(enabled: Boolean) {
        try {
            Log.d("AlarmSettingsUseCase", "사진 리마인더 설정 변경: $enabled")
            albumRepository.setPhotoReminderEnabled(enabled)
            Log.d("AlarmSettingsUseCase", "사진 리마인더 설정 저장 완료")
        } catch (e: Exception) {
            Log.e("AlarmSettingsUseCase", "사진 리마인더 설정 저장 실패", e)
            throw e
        }
    }

    suspend fun isPhotoReminderEnabled(): Flow<Boolean> {
        return albumRepository.isPhotoReminderEnabled()
            .catch { exception ->
                Log.e("AlarmSettingsUseCase", "사진 리마인더 설정 조회 실패", exception)
                emit(true) // 기본값
            }
    }

    // 배송 알림 설정
    suspend fun setDeliveryNotificationEnabled(enabled: Boolean) {
        try {
            Log.d("AlarmSettingsUseCase", "배송 알림 설정 변경: $enabled")
            albumRepository.setDeliveryNotificationEnabled(enabled)
            Log.d("AlarmSettingsUseCase", "배송 알림 설정 저장 완료")
        } catch (e: Exception) {
            Log.e("AlarmSettingsUseCase", "배송 알림 설정 저장 실패", e)
            throw e
        }
    }

    suspend fun isDeliveryNotificationEnabled(): Flow<Boolean> {
        return albumRepository.isDeliveryNotificationEnabled()
            .catch { exception ->
                Log.e("AlarmSettingsUseCase", "배송 알림 설정 조회 실패", exception)
                emit(true) // 기본값
            }
    }

    // 마케팅 알림 설정
    suspend fun setMarketingNotificationEnabled(enabled: Boolean) {
        try {
            Log.d("AlarmSettingsUseCase", "마케팅 알림 설정 변경: $enabled")
            albumRepository.setMarketingNotificationEnabled(enabled)
            Log.d("AlarmSettingsUseCase", "마케팅 알림 설정 저장 완료")
        } catch (e: Exception) {
            Log.e("AlarmSettingsUseCase", "마케팅 알림 설정 저장 실패", e)
            throw e
        }
    }

    suspend fun isMarketingNotificationEnabled(): Flow<Boolean> {
        return albumRepository.isMarketingNotificationEnabled()
            .catch { exception ->
                Log.e("AlarmSettingsUseCase", "마케팅 알림 설정 조회 실패", exception)
                emit(true) // 기본값
            }
    }

    // 마지막 사진 등록 날짜 관리
    suspend fun updateLastPhotoDate(date: String) {
        try {
            Log.d("AlarmSettingsUseCase", "마지막 사진 등록 날짜 업데이트: $date")
            albumRepository.updateLastPhotoDate(date)
            Log.d("AlarmSettingsUseCase", "마지막 사진 등록 날짜 저장 완료")
        } catch (e: Exception) {
            Log.e("AlarmSettingsUseCase", "마지막 사진 등록 날짜 저장 실패", e)
            throw e
        }
    }

    suspend fun getLastPhotoDate(): Flow<String?> {
        return albumRepository.getLastPhotoDate()
            .catch { exception ->
                Log.e("AlarmSettingsUseCase", "마지막 사진 등록 날짜 조회 실패", exception)
                emit(null) // 기본값
            }
    }

    // 모든 알림 설정을 한 번에 조회하는 편의 메서드
    data class AlarmSettings(
        val photoReminderEnabled: Boolean,
        val deliveryNotificationEnabled: Boolean,
        val marketingNotificationEnabled: Boolean
    )
}
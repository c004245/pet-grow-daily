package kr.co.hyunwook.pet_grow_daily.feature.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmSettingsUseCase: AlarmSettingsUseCase
    // TODO: 추후 PhotoReminderScheduler를 의존성 주입
    // private val photoReminderScheduler: PhotoReminderScheduler
) : ViewModel() {

    private val _photoReminderEnabled = MutableStateFlow(true)
    val photoReminderEnabled: StateFlow<Boolean> = _photoReminderEnabled.asStateFlow()

    private val _deliveryNotificationEnabled = MutableStateFlow(true)
    val deliveryNotificationEnabled: StateFlow<Boolean> = _deliveryNotificationEnabled.asStateFlow()

    private val _marketingNotificationEnabled = MutableStateFlow(true)
    val marketingNotificationEnabled: StateFlow<Boolean> =
        _marketingNotificationEnabled.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAlarmSettings()
    }

    private fun loadAlarmSettings() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // 사진 리마인더 설정 로드
                alarmSettingsUseCase.isPhotoReminderEnabled()
                    .collect { enabled ->
                        _photoReminderEnabled.value = enabled
                    }

                // 배송 알림 설정 로드
                alarmSettingsUseCase.isDeliveryNotificationEnabled()
                    .collect { enabled ->
                        _deliveryNotificationEnabled.value = enabled
                    }

                // 마케팅 알림 설정 로드
                alarmSettingsUseCase.isMarketingNotificationEnabled()
                    .collect { enabled ->
                        _marketingNotificationEnabled.value = enabled
                    }

                Log.d("AlarmViewModel", "알림 설정 로드 완료")
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "알림 설정 로드 중 오류 발생", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setPhotoReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("AlarmViewModel", "사진 리마인더 설정 변경: $enabled")

                _photoReminderEnabled.value = enabled

                // UseCase를 통해 저장
                alarmSettingsUseCase.setPhotoReminderEnabled(enabled)

                // TODO: 스케줄링 설정/해제
                // if (enabled) {
                //     photoReminderScheduler.schedulePhotoReminder()
                // } else {
                //     photoReminderScheduler.cancelPhotoReminder()
                // }

                Log.d("AlarmViewModel", "사진 리마인더 설정 저장 완료")
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "사진 리마인더 설정 저장 실패", e)
                // 실패 시 이전 상태로 복원
                _photoReminderEnabled.value = !enabled
            }
        }
    }

    fun setDeliveryNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("AlarmViewModel", "배송 알림 설정 변경: $enabled")

                _deliveryNotificationEnabled.value = enabled

                // UseCase를 통해 저장
                alarmSettingsUseCase.setDeliveryNotificationEnabled(enabled)

                Log.d("AlarmViewModel", "배송 알림 설정 저장 완료")
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "배송 알림 설정 저장 실패", e)
                _deliveryNotificationEnabled.value = !enabled
            }
        }
    }

    fun setMarketingNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                Log.d("AlarmViewModel", "마케팅 알림 설정 변경: $enabled")

                _marketingNotificationEnabled.value = enabled

                // UseCase를 통해 저장
                alarmSettingsUseCase.setMarketingNotificationEnabled(enabled)

                Log.d("AlarmViewModel", "마케팅 알림 설정 저장 완료")
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "마케팅 알림 설정 저장 실패", e)
                _marketingNotificationEnabled.value = !enabled
            }
        }
    }
}
package kr.co.hyunwook.pet_grow_daily.feature.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import kr.co.hyunwook.pet_grow_daily.util.alarm.PhotoReminderScheduler
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val alarmSettingsUseCase: AlarmSettingsUseCase,
    private val photoReminderScheduler: PhotoReminderScheduler
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
        _isLoading.value = true

        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isPhotoReminderEnabled()
                    .collect { enabled ->
                        _photoReminderEnabled.value = enabled
                    }
            } catch (e: Exception) {
                _photoReminderEnabled.value = true // 기본값
            }
        }

        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isDeliveryNotificationEnabled()
                    .collect { enabled ->
                        _deliveryNotificationEnabled.value = enabled
                    }
            } catch (e: Exception) {
                _deliveryNotificationEnabled.value = true // 기본값
            }
        }

        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isMarketingNotificationEnabled()
                    .collect { enabled ->
                        _marketingNotificationEnabled.value = enabled
                    }
            } catch (e: Exception) {
                _marketingNotificationEnabled.value = true // 기본값
            }
        }

        // 초기 로딩 완료 처리
        viewModelScope.launch {
            try {
                // 모든 설정의 첫 값을 기다린 후 로딩 상태 해제
                alarmSettingsUseCase.isPhotoReminderEnabled().first()
                alarmSettingsUseCase.isDeliveryNotificationEnabled().first()
                alarmSettingsUseCase.isMarketingNotificationEnabled().first()

                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun setPhotoReminderEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _photoReminderEnabled.value = enabled
                alarmSettingsUseCase.setPhotoReminderEnabled(enabled)

                 if (enabled) {
                     photoReminderScheduler.schedulePhotoReminder()
                 } else {
                     photoReminderScheduler.cancelPhotoReminder()
                 }

            } catch (e: Exception) {
                _photoReminderEnabled.value = !enabled
            }
        }
    }

    fun setDeliveryNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _deliveryNotificationEnabled.value = enabled

                // UseCase를 통해 저장
                alarmSettingsUseCase.setDeliveryNotificationEnabled(enabled)
            } catch (e: Exception) {
                _deliveryNotificationEnabled.value = !enabled
            }
        }
    }

    fun setMarketingNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                _marketingNotificationEnabled.value = enabled
                alarmSettingsUseCase.setMarketingNotificationEnabled(enabled)
            } catch (e: Exception) {
                _marketingNotificationEnabled.value = !enabled
            }
        }
    }
}
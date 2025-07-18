package kr.co.hyunwook.pet_grow_daily.feature.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
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
        Log.d("AlarmViewModel", "알림 설정 로드 시작")

        _isLoading.value = true

        // 각 설정을 별도 코루틴에서 독립적으로 수집
        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isPhotoReminderEnabled()
                    .collect { enabled ->
                        _photoReminderEnabled.value = enabled
                        Log.d("AlarmViewModel", "사진 리마인더 설정: $enabled")
                    }
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "사진 리마인더 설정 로드 실패", e)
                _photoReminderEnabled.value = true // 기본값
            }
        }

        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isDeliveryNotificationEnabled()
                    .collect { enabled ->
                        _deliveryNotificationEnabled.value = enabled
                        Log.d("AlarmViewModel", "배송 알림 설정: $enabled")
                    }
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "배송 알림 설정 로드 실패", e)
                _deliveryNotificationEnabled.value = true // 기본값
            }
        }

        viewModelScope.launch {
            try {
                alarmSettingsUseCase.isMarketingNotificationEnabled()
                    .collect { enabled ->
                        _marketingNotificationEnabled.value = enabled
                        Log.d("AlarmViewModel", "마케팅 알림 설정: $enabled")
                    }
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "마케팅 알림 설정 로드 실패", e)
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
                Log.d("AlarmViewModel", "알림 설정 로드 완료")
            } catch (e: Exception) {
                Log.e("AlarmViewModel", "알림 설정 로드 중 오류 발생", e)
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


                 if (enabled) {
                     photoReminderScheduler.scheduleImmediateTest()
                 } else {
                     photoReminderScheduler.cancelPhotoReminder()
                 }

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
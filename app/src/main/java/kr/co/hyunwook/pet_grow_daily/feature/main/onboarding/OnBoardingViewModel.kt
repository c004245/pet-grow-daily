package kr.co.hyunwook.pet_grow_daily.feature.main.onboarding

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveLoginStateUseCase
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.HasPetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import kr.co.hyunwook.pet_grow_daily.util.alarm.PhotoReminderScheduler
import android.util.Log

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveLoginStateUseCase: SaveLoginStateUseCase,
    private val hasPetProfileUseCase: HasPetProfileUseCase,
    private val alarmSettingsUseCase: AlarmSettingsUseCase,
    private val photoReminderScheduler: PhotoReminderScheduler
): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    fun handleKakaoLoginSuccess(userId: Long, nickName: String?, email: String?) {
        viewModelScope.launch {
            try {
                saveLoginStateUseCase(userId, nickName, email)

                // 로그인 성공 시 알림 설정 초기화
                initializeAlarmSettings()

                hasPetProfileUseCase().collect { hasPetProfile ->
                    if (hasPetProfile) {
                        _loginState.value = LoginState.SuccessAlbum
                    } else {
                        _loginState.value = LoginState.SuccessProfile
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun initializeAlarmSettings() {
        try {


            alarmSettingsUseCase.setPhotoReminderEnabled(true)
            photoReminderScheduler.schedulePhotoReminder()


            alarmSettingsUseCase.setDeliveryNotificationEnabled(true)
            // 마케팅 알림 활성
            alarmSettingsUseCase.setMarketingNotificationEnabled(true)

            Log.d("OnBoardingViewModel", "알림 설정 초기화 완료")
        } catch (e: Exception) {
            Log.e("OnBoardingViewModel", "알림 설정 초기화 실패", e)
        }
    }

    sealed class LoginState {
        object Initial: LoginState()
        object SuccessAlbum: LoginState()
        object SuccessProfile: LoginState()
        data class Error(val message: String): LoginState()
    }
}

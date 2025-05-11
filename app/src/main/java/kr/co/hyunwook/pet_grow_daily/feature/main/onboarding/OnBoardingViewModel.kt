package kr.co.hyunwook.pet_grow_daily.feature.main.onboarding

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveLoginStateUseCase
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveLoginStateUseCase: SaveLoginStateUseCase

): ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState: StateFlow<LoginState> = _loginState

    fun handleKakaoLoginSuccess(userId: Long) {
        viewModelScope.launch {
            try {
                saveLoginStateUseCase(userId)
                _loginState.value = LoginState.Success
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }

    }

    sealed class LoginState {
        object Initial: LoginState()
        object Success: LoginState()
        data class Error(val message: String): LoginState()
    }
}

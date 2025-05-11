package kr.co.hyunwook.pet_grow_daily.feature.main.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetHasCompletedOnBoardingUseCase
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getHasCompletedOnBoardingUseCase: GetHasCompletedOnBoardingUseCase
) : ViewModel() {
    private val _sideEffects = MutableSharedFlow<SplashSideEffect>()
    val sideEffects: SharedFlow<SplashSideEffect> get() = _sideEffects.asSharedFlow()

    fun showSplash() {
        viewModelScope.launch {
            delay(SPLASH_DURATION)
            getHasCompletedOnBoardingUseCase.invoke().collect { isComplete ->
                if (isComplete) {
                    _sideEffects.emit(SplashSideEffect.NavigateToAlbum)
                } else {
                    _sideEffects.emit(SplashSideEffect.NavigateToOnBoarding)
                }
            }

        }
    }

    companion object {
        const val SPLASH_DURATION = 500L
    }
}
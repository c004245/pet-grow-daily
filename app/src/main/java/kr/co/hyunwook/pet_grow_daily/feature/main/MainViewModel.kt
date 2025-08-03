package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveFcmTokenUseCase
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveFcmTokenUseCase: SaveFcmTokenUseCase
) : ViewModel() {

     fun saveFcmToken(token: String) {
        viewModelScope.launch {
            saveFcmTokenUseCase.invoke(fcmToken = token)

        }
    }
}



package kr.co.hyunwook.pet_grow_daily.feature.order

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserAlbumCountUseCase
import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getUserAlbumCountUseCase: GetUserAlbumCountUseCase

): ViewModel() {

    private val _userAlbumCount = MutableStateFlow<Int>(0)
    val userAlbumCount: StateFlow<Int> get() = _userAlbumCount

    fun getUserAlbumCount() {
        viewModelScope.launch {
            val userAlbumCount = getUserAlbumCountUseCase()
            Log.d("HWO", "userAlbumCount -> $userAlbumCount")
                _userAlbumCount.value = userAlbumCount
            }
        }
    }

package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(

) : ViewModel() {


    private val _saveNameEvent = MutableSharedFlow<Boolean>()
    val saveNameEvent: SharedFlow<Boolean> get() = _saveNameEvent

}
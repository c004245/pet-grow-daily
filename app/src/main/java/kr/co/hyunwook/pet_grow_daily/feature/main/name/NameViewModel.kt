package kr.co.hyunwook.pet_grow_daily.feature.main.name

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NameViewModel @Inject constructor(
    private val saveNameUseCase: SaveNameUseCase

) : ViewModel() {


    private val _saveNameEvent = MutableSharedFlow<Boolean>()
    val saveNameEvent: SharedFlow<Boolean> get() = _saveNameEvent

    fun saveDogName(dogName: String) {
        flow {
            emit(saveNameUseCase(dogName))
        }.onEach {
            _saveNameEvent.emit(true)
        }.catch {
            _saveNameEvent.emit(false)
        }.launchIn(viewModelScope)

    }
}
package kr.co.hyunwook.pet_grow_daily.feature.anotherpet

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AnotherPetModel
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAnotherPetImageUseCase
import android.util.Log
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

@HiltViewModel
class AnotherPetViewModel @Inject constructor(
    private val getAnotherPetImageUseCase: GetAnotherPetImageUseCase

): ViewModel() {

    private val _anotherPetList = MutableStateFlow<List<AnotherPetModel>>(emptyList())
    val anotherPetList: StateFlow<List<AnotherPetModel>> get() = _anotherPetList

    fun getAnotherPetList() {
        viewModelScope.launch {
            getAnotherPetImageUseCase().collect { images ->
                Log.d("HWO", "getAnotherPet -> ${images.size}")
                _anotherPetList.value = images
            }
        }
    }
}

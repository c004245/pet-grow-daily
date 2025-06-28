package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.DeleteDeliveryInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetDeliveryInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetDeliveryListUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveDeliveryInfoUseCase
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getDeliveryListUseCase: GetDeliveryListUseCase,
    private val saveDeliveryInfoUseCase: SaveDeliveryInfoUseCase,
    private val deleteDeliveryInfoUseCase: DeleteDeliveryInfoUseCase,
    private val getDeliveryInfoUseCase: GetDeliveryInfoUseCase

) : ViewModel() {

    private val _deliveryInfos = MutableStateFlow<List<DeliveryInfo>>(emptyList())
    val deliveryInfos: StateFlow<List<DeliveryInfo>> get() = _deliveryInfos

    private val _saveDeliveryDoneEvent = MutableSharedFlow<Boolean>()
    val saveDeliveryDoneEvent: SharedFlow<Boolean> get() = _saveDeliveryDoneEvent

    private val _selectedDeliveryInfo = MutableStateFlow<DeliveryInfo?>(null)
    val selectedDeliveryInfo: StateFlow<DeliveryInfo?> get() = _selectedDeliveryInfo

    fun getDeliveryList() {
        viewModelScope.launch {
            getDeliveryListUseCase().collect {
                _deliveryInfos.value = it
            }
        }
    }

    fun getDeliveryInfoById(id: Int) {
        viewModelScope.launch {
            getDeliveryInfoUseCase(id).collect {
                _selectedDeliveryInfo.value = it
            }
        }
    }

    fun clearSelectedDeliveryInfo() {
        _selectedDeliveryInfo.value = null
    }

    fun deleteDeliveryInfo(id: Int) {
        viewModelScope.launch {
            try {
                deleteDeliveryInfoUseCase(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveDeliveryInfo(deliveryInfo: DeliveryInfo) {
        viewModelScope.launch {
            try {
                saveDeliveryInfoUseCase(deliveryInfo)
                _saveDeliveryDoneEvent.emit(true)
            } catch (e: Exception) {
                _saveDeliveryDoneEvent.emit(false)
            }
        }
    }
}

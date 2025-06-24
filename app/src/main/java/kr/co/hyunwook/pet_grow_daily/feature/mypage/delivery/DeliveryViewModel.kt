package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetDeliveryListUseCase
import javax.inject.Inject

@HiltViewModel
class DeliveryViewModel @Inject constructor(
    private val getDeliveryListUseCase: GetDeliveryListUseCase

): ViewModel() {

    private val _deliveryInfos = MutableStateFlow<List<DeliveryInfo>>(emptyList())
    val deliveryInfos: StateFlow<List<DeliveryInfo>> get() = _deliveryInfos


    fun getDeliveryList() {
        viewModelScope.launch {
            getDeliveryListUseCase().collect {
                _deliveryInfos.value = it
            }
        }

    }
}
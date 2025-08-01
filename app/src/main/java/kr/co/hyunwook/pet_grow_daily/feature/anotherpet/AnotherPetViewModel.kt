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
import com.google.firebase.firestore.DocumentSnapshot

@HiltViewModel
class AnotherPetViewModel @Inject constructor(
    private val getAnotherPetImageUseCase: GetAnotherPetImageUseCase
): ViewModel() {

    private val _anotherPetList = MutableStateFlow<List<AnotherPetModel>>(emptyList())
    val anotherPetList: StateFlow<List<AnotherPetModel>> get() = _anotherPetList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _hasMoreData = MutableStateFlow(true)
    val hasMoreData: StateFlow<Boolean> get() = _hasMoreData

    private var lastDocument: DocumentSnapshot? = null
    private val pageSize = 30

    fun getAnotherPetList() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                val (newItems, newLastDocument) = getAnotherPetImageUseCase(
                    pageSize = pageSize,
                    lastDocument = null
                )
                _anotherPetList.value = newItems
                lastDocument = newLastDocument
                _hasMoreData.value = newItems.size >= pageSize


            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMoreData() {
        if (_isLoading.value || !_hasMoreData.value) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val (newItems, newLastDocument) = getAnotherPetImageUseCase(
                    pageSize = pageSize,
                    lastDocument = lastDocument
                )

                val currentList = _anotherPetList.value.toMutableList()
                currentList.addAll(newItems)
                _anotherPetList.value = currentList

                lastDocument = newLastDocument
                _hasMoreData.value = newItems.size >= pageSize


            } catch (e: Exception) {
                Log.e("HWO", "추가 페이지 로드 실패: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}

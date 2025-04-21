package kr.co.hyunwook.pet_grow_daily.feature.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetTodayGrowRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getTodayGrowRecordUseCase: GetTodayGrowRecordUseCase

): ViewModel() {

    private val _todayGrowRecords = MutableStateFlow<List<GrowRecord>>(emptyList())
    val todayGrowRecords: StateFlow<List<GrowRecord>> get() = _todayGrowRecords

    fun getGrowRecord(todayDate: String) {
        viewModelScope.launch {
            getTodayGrowRecordUseCase(todayDate).collect { records ->
                _todayGrowRecords.value = records
            }
        }
    }
}
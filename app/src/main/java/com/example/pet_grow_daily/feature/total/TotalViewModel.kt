package com.example.pet_grow_daily.feature.total

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.core.domain.usecase.GetMonthlyGrowRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TotalViewModel @Inject constructor(
    private val getMonthlyGrowRecordUseCase: GetMonthlyGrowRecordUseCase
): ViewModel() {

    private val _monthlyGrowRecords = MutableStateFlow<List<GrowRecord>>(emptyList())
    val monthlyGrowRecords: StateFlow<List<GrowRecord>> get() = _monthlyGrowRecords


    fun getMonthlyRecord(month: String) {
        viewModelScope.launch {
            getMonthlyGrowRecordUseCase(month).collect { records ->
                _monthlyGrowRecords.value = records
            }
        }
    }
}
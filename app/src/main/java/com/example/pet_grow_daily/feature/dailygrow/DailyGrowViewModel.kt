package com.example.pet_grow_daily.feature.dailygrow

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.core.domain.usecase.GetTodayGrowRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DailyGrowViewModel @Inject constructor(
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
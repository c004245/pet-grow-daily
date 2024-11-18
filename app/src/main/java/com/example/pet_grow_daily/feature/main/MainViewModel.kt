package com.example.pet_grow_daily.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pet_grow_daily.core.data.repository.grow.GrowRepository
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.core.domain.usecase.SaveGrowRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val saveGrowRecordUseCase: SaveGrowRecordUseCase
): ViewModel() {
    private val _saveDoneEvent = MutableSharedFlow<Boolean>()
    val saveDoneEvent: SharedFlow<Boolean> get() = _saveDoneEvent

    fun saveGrowRecord(growRecord: GrowRecord) {
        flow {
            emit(saveGrowRecordUseCase(growRecord))
        }.onEach {

        }.catch {

        }.launchIn(viewModelScope)

    }
}



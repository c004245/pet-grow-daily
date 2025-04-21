package kr.co.hyunwook.pet_grow_daily.feature.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAlbumRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase

): ViewModel() {

    private val _albumRecord = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val albumRecord: StateFlow<List<AlbumRecord>> get() = _albumRecord


    fun getAlbumRecord() {
        viewModelScope.launch {
            getAlbumRecordUseCase().collect { records ->
                _albumRecord.value = records
            }
        }
    }
}
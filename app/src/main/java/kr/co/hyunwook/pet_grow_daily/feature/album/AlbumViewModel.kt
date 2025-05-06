package kr.co.hyunwook.pet_grow_daily.feature.album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAlbumRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAllImageUseCase
import kr.co.hyunwook.pet_grow_daily.feature.albumimage.navigation.AlbumImage
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase,
    private val getAllImageUseCase: GetAllImageUseCase
): ViewModel() {

    private val _albumRecord = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val albumRecord: StateFlow<List<AlbumRecord>> get() = _albumRecord


    private val _albumImageList = MutableStateFlow<List<AlbumImageModel>>(emptyList())
    val albumImageList: StateFlow<List<AlbumImageModel>> get() = _albumImageList

    fun getAlbumRecord() {
        viewModelScope.launch {
            getAlbumRecordUseCase().collect { records ->
                _albumRecord.value = records
            }
        }
    }

    fun getAlbumImageList() {
        viewModelScope.launch {
            getAllImageUseCase().collect { images ->
                _albumImageList.value = images
            }
        }
    }
}
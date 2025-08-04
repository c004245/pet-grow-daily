package kr.co.hyunwook.pet_grow_daily.feature.album

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAlbumRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumImageModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetAllImageUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetShouldDisableUploadUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetTodayUserPhotoCountUseCase
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val getAlbumRecordUseCase: GetAlbumRecordUseCase,
    private val getAllImageUseCase: GetAllImageUseCase,
    private val getTodayUserPhotoCountUseCase: GetTodayUserPhotoCountUseCase,
    private val getShouldDisableUploadUseCase: GetShouldDisableUploadUseCase,
) : ViewModel() {

    private val _albumRecord = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val albumRecord: StateFlow<List<AlbumRecord>> get() = _albumRecord


    private val _albumImageList = MutableStateFlow<List<AlbumImageModel>>(emptyList())
    val albumImageList: StateFlow<List<AlbumImageModel>> get() = _albumImageList

    private val _todayUserPhotoCount = MutableStateFlow<Int>(0)
    val todayUserPhotoCount: StateFlow<Int> get() = _todayUserPhotoCount


    private val _isDisableUpload = MutableStateFlow<Boolean>(false)
    val isDisableUpload: StateFlow<Boolean> get() = _isDisableUpload

    fun getShouldDisableUpload() {
        viewModelScope.launch {
            val isDisable = getShouldDisableUploadUseCase()
            Log.d("HWO", "isDisable -> $isDisable -- ")
            _isDisableUpload.value = isDisable
        }

    }

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

    fun getTodayUserPhotoCount() {
        viewModelScope.launch {
            try {
                val count = getTodayUserPhotoCountUseCase()
                Log.d("HWO", "오늘 오늘 유저수 : $count")
                _todayUserPhotoCount.value = count
            } catch (e: Exception) {
                Log.e("HWO", "오늘 유저", e)
                _todayUserPhotoCount.value = 0
            }
        }

    }
}
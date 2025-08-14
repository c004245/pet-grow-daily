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
import kr.co.hyunwook.pet_grow_daily.analytics.Analytics
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants
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
    private val analytics: Analytics
) : ViewModel() {

    private val _albumRecord = MutableStateFlow<List<AlbumRecord>>(emptyList())
    val albumRecord: StateFlow<List<AlbumRecord>> get() = _albumRecord


    private val _albumImageList = MutableStateFlow<List<AlbumImageModel>>(emptyList())
    val albumImageList: StateFlow<List<AlbumImageModel>> get() = _albumImageList

    private val _todayUserPhotoCount = MutableStateFlow<Int>(0)
    val todayUserPhotoCount: StateFlow<Int> get() = _todayUserPhotoCount


    private val _isDisableUpload = MutableStateFlow<Boolean>(false)
    val isDisableUpload: StateFlow<Boolean> get() = _isDisableUpload

    private val _isRecordLoading = MutableStateFlow(true)
    val isRecordLoading: StateFlow<Boolean> get() = _isRecordLoading

    private val _isImageLoading = MutableStateFlow(false)
    val isImageLoading: StateFlow<Boolean> get() = _isImageLoading

    // 세션(프로세스)당 1회만 이벤트를 전송하기 위한 가드 플래그
    private var hasLoggedAlbumEnter: Boolean = false

    fun getShouldDisableUpload() {
        viewModelScope.launch {
            val isDisable = getShouldDisableUploadUseCase()
            Log.d("HWO", "isDisable -> $isDisable -- ")
            _isDisableUpload.value = isDisable
        }

    }

    fun getAlbumRecord() {
        viewModelScope.launch {
            _isRecordLoading.value = true
            getAlbumRecordUseCase().collect { records ->
                _albumRecord.value = records
                _isRecordLoading.value = false

                // albumRecord를 최초로 가져온 직후 1회만 이벤트 로깅
                if (!hasLoggedAlbumEnter) {
                    Log.d("HWO", "앨범 탭 진입 이벤트(1회): albumRecord 로드 완료")
                    enterAlbumTabEvent(albumRecord.value.size)
                    hasLoggedAlbumEnter = true
                }
            }
        }
    }

    fun enterAlbumTabEvent(count: Int) {
        analytics.track(
            EventConstants.ENTER_ALBUM_TAB_EVENT,
            mapOf(
                EventConstants.ALBUM_COUNT_PROPERTY to count)
            )

    }


    fun getAlbumImageList() {
        viewModelScope.launch {
            _isImageLoading.value = true
            getAllImageUseCase().collect { images ->
                _albumImageList.value = images
                _isImageLoading.value = false
            }
        }
    }

    fun getTodayUserPhotoCount() {
        viewModelScope.launch {
            _isRecordLoading.value = true
            try {
                val count = getTodayUserPhotoCountUseCase()
                Log.d("HWO", "오늘 오늘 유저수 : $count")
                _todayUserPhotoCount.value = count
            } catch (e: Exception) {
                Log.e("HWO", "오늘 유저", e)
                _todayUserPhotoCount.value = 0
            }
            _isRecordLoading.value = false

        }

    }
}
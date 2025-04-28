package kr.co.hyunwook.pet_grow_daily.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveAlbumRecordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
) : ViewModel() {

}



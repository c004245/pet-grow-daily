package kr.co.hyunwook.pet_grow_daily.feature.mypage

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetPetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserInfoUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SavePetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.util.deleteFirebaseProfileImage
import kr.co.hyunwook.pet_grow_daily.util.optimizeImageForAlbumCover
import kr.co.hyunwook.pet_grow_daily.util.uploadProfileImageToFirebase
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

@HiltViewModel
class MyPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getPetProfileUseCase: GetPetProfileUseCase,
    private val savePetProfileUseCase: SavePetProfileUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val getUserIdUseCase: GetUserIdUseCase

) : ViewModel() {

    
    val petProfile: StateFlow<PetProfile?> = flow {
        emit(Unit)
    }.flatMapLatest {
        getPetProfileUseCase()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    private val _userInfo = MutableStateFlow<Pair<String?, String?>>(value = Pair("견주", "DailyDog"))
    val userInfo: StateFlow<Pair<String?, String?>> = _userInfo.asStateFlow()

    init {
        viewModelScope.launch {
            val userInfoData = getUserInfoUseCase()
            Log.d("HWO", "userInfoData -> $userInfoData")
            _userInfo.value = userInfoData
        }
    }

    fun updateProfileImage(imageUri: String) {
        viewModelScope.launch {
            try {
                val currentProfile = petProfile.value

                try {
                    currentProfile?.profileImageUrl?.let { oldUrl ->
                        if (oldUrl.isNotEmpty() && oldUrl.contains("firebase")) {
                            deleteFirebaseProfileImage(oldUrl)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("MyPageViewModel", "기존 프로필 조회 실패: ${e.message}")
                }

                val userId = getUserIdUseCase.invoke()

                val profileImageUrl = uploadProfileImageToFirebase(context, Uri.parse(imageUri), userId.toString())

                if (currentProfile != null) {
                    val updatedProfile = currentProfile.copy(profileImageUrl = profileImageUrl)
                    savePetProfileUseCase(updatedProfile)
                } else {
                    val newProfile = PetProfile(
                        name = "사용자",
                        profileImageUrl = profileImageUrl
                    )
                    savePetProfileUseCase(newProfile)
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "프로필 이미지 업데이트 실패: ${e.message}", e)
            }
        }
    }


}

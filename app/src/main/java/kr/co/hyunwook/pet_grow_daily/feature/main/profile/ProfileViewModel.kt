package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetPetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.GetUserIdUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SavePetProfileUseCase
import kr.co.hyunwook.pet_grow_daily.util.deleteFirebaseProfileImage
import kr.co.hyunwook.pet_grow_daily.util.uploadProfileImageToFirebase
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val savePetProfileUseCase: SavePetProfileUseCase,
    private val getPetProfileUseCase: GetPetProfileUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {

    private val _saveProfileEvent = MutableSharedFlow<Boolean>()
    val saveProfileEvent: SharedFlow<Boolean> get() = _saveProfileEvent

    fun saveProfile(name: String, imageUrl: String?) {
        viewModelScope.launch {
            try {
                val profileImageUrl = if (imageUrl != null) {
                    try {
                        val currentProfile = getPetProfileUseCase().first()
                        currentProfile?.profileImageUrl?.let { oldUrl ->
                            if (oldUrl.isNotEmpty() && oldUrl.contains("firebase")) {
                                deleteFirebaseProfileImage(oldUrl)
                            }
                        }
                    } catch (e: Exception) {
                        Log.d("ProfileViewModel", "기존 프로필 조회 실패: ${e.message}")
                    }

                    val userId = getUserIdUseCase.invoke()

                    uploadProfileImageToFirebase(context, Uri.parse(imageUrl), userId.toString())
                } else {
                    ""
                }
                val profile = PetProfile(
                    name = name,
                    profileImageUrl = profileImageUrl
                )

                savePetProfileUseCase(profile)
                _saveProfileEvent.emit(true)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "프로필 저장 실패: ${e.message}", e)
                _saveProfileEvent.emit(false)
            }
        }
    }



}

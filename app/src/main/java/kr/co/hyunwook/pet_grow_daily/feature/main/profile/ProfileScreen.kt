package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleC4
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun ProfileScreen(
    navigateToAlbum: () -> Unit = {},
    profileViewModel: ProfileViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )




    var nameText by remember {
        mutableStateOf("")
    }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        profileViewModel.saveProfileEvent.collect { isSuccess ->
            isUploading = false
            if (isSuccess) {
                navigateToAlbum()
            } else {
                Toast.makeText(context, "저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false
        ) { page ->
            when (page) {
                0 -> ProfileNamePage(
                    nameText = nameText,
                    onNameChange = { nameText = it },
                    onNextClick = {
                        coroutineScope.launch {
                            focusManager.clearFocus()
                            pagerState.animateScrollToPage(1)
                        }

                    }
                )
                1 -> ProfileImagePage(
                    selectedImageUri = selectedImageUri,
                    onImageSelected = { selectedImageUri = it },
                    onCompleteClick = {
                        isUploading = true
                        profileViewModel.saveProfile(
                            name = nameText,
                            imageUrl = selectedImageUri
                        )
                    }
                )
            }
        }
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = purple6C,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "프로필을 저장하는 중입니다...",
                        style = PetgrowTheme.typography.medium,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }





}

package kr.co.hyunwook.pet_grow_daily.feature.main.onboarding

import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnBoardingScreen(
    navigateToAlbum: () -> Unit = {},
    viewModel: OnBoardingViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is OnBoardingViewModel.LoginState.Success) {
            navigateToAlbum()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(id = R.string.text_onboarding_title),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.text_onboarding_description),
                style = PetgrowTheme.typography.light,
                color = black21,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_onboarding_background),
                contentDescription = "background"
            )
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_kakao_button),
                contentDescription = null,
                modifier = Modifier.clickable {
                    handleKakaoLogin(
                        context = context,
                        onSuccess = { userId ->
                            Log.d("HWO", "userId -> $userId")
                            viewModel.handleKakaoLoginSuccess(userId)
                        },
                        onError = { error ->
                            Log.d(
                                "HWO", "카카오 로그인 실패 -> $error"
                            )
                        }
                    )
                }
            )
        }
    }
}


private fun handleKakaoLogin(
    context: Context,
    onSuccess: (Long) -> Unit,
    onError: (Throwable) -> Unit
) {
    if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
        UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    onError(error)
                    return@loginWithKakaoTalk
                }
                loginWithKakaoAccount(context, onSuccess, onError)
            } else if (token != null) {
                getUserInfo(onSuccess, onError)
            }
        }
    } else {
        loginWithKakaoAccount(context, onSuccess, onError)
    }
}

private fun loginWithKakaoAccount(
    context: Context,
    onSuccess: (Long) -> Unit,
    onError: (Throwable) -> Unit
) {
    UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
        if (error != null) {
            onError(error)
        } else if (token != null) {
            getUserInfo(onSuccess, onError)
        }
    }
}

private fun getUserInfo(
    onSuccess: (Long) -> Unit,
    onError: (Throwable) -> Unit
) {
    UserApiClient.instance.me { user, error ->
        if (error != null) {
            onError(error)
        } else if (user?.id != null) {  // user.id가 null이 아닌 경우에만 성공
            // 사용자의 고유 ID 전달
            onSuccess(user.id!!)
        } else {
            // user가 null이거나 user.id가 null인 경우 오류로 처리
            val errorMessage = "사용자 ID를 가져올 수 없습니다."
            onError(RuntimeException(errorMessage))
        }
    }
}
@Preview
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen()
}

data class OnBoardingPage(
    val title: String,
    val description: String,
    val imageRes: Int
)
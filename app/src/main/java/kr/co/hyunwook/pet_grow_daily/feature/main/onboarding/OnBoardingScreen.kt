package kr.co.hyunwook.pet_grow_daily.feature.main.onboarding

import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
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
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black29
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.main.onboarding.navigation.OnBoarding
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OnBoardingScreen(
    navigateToAlbum: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    viewModel: OnBoardingViewModel = hiltViewModel()
) {

    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(loginState) {
        if (loginState is OnBoardingViewModel.LoginState.SuccessAlbum) {
            navigateToAlbum()
        } else if (loginState is OnBoardingViewModel.LoginState.SuccessProfile) {
            navigateToProfile()
        }
    }
    var pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { 4 })

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            Box(modifier = Modifier.weight(1f)) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    OnBoardingItem(page)
                }
                PageIndicator(
                    pageCount = 4,
                    currentPage = pagerState.currentPage,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 30.dp)
                )
            }
            OnBoardingBottomButton(
                currentPage = pagerState.currentPage,
                onKakaoLogin = {
                    handleKakaoLogin(
                        context = context,
                        onSuccess = { userId, nickName, email  ->
                            Log.d("HWO", "userId -> $userId -- $nickName -- $email")
                            viewModel.handleKakaoLoginSuccess(userId, nickName, email)
                        },
                        onError = { error ->
                            Log.d(
                                "HWO", "카카오 로그인 실패 -> $error"
                            )
                        }
                    )
                },
                onNextClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            )
        }
    }
}

@Composable
fun OnBoardingItem(pageIndex: Int) {
    val pages = listOf(
        OnBoardingPage(stringResource(R.string.text_onboarding_title1), R.drawable.ic_onboarding_1),
        OnBoardingPage(stringResource(R.string.text_onboarding_title2), R.drawable.ic_onboarding_2),
        OnBoardingPage(stringResource(R.string.text_onboarding_title3), R.drawable.ic_onboarding_3),
        OnBoardingPage(stringResource(R.string.text_onboarding_title4), R.drawable.ic_onboarding_4),
    )

    val currentPage = pages[pageIndex]

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = currentPage.title,
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
        Image(painter = painterResource(currentPage.imageRes),
            contentDescription = null)
    }

}

@Composable
fun PageIndicator(pageCount: Int, currentPage: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (index == currentPage) black29 else black29.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
fun OnBoardingBottomButton(
    currentPage: Int,
    onKakaoLogin: () -> Unit,
    onNextClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (currentPage == 3) {
            Image(
                painter = painterResource(id = R.drawable.ic_kakao_button),
                contentDescription = "카카오 로그인",
                modifier = Modifier.clickable { onKakaoLogin() }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp)
                    .clickable { onNextClick() }
                    .clip(RoundedCornerShape(14.dp))
                    .background(purple6C)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.text_next),
                        color = Color.White,
                        fontSize = 14.sp,
                        style = PetgrowTheme.typography.medium,
                    )
                }
            }
        }
    }
}

private fun handleKakaoLogin(
    context: Context,
    onSuccess: (Long, String?, String?) -> Unit,
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
    onSuccess: (Long, String?, String?) -> Unit,
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
    onSuccess: (Long, String?, String?) -> Unit,
    onError: (Throwable) -> Unit
) {
    UserApiClient.instance.me { user, error ->
        if (error != null) {
            onError(error)
        } else if (user?.id != null) {
            onSuccess(user.id!!, user.kakaoAccount?.profile?.nickname, user.kakaoAccount?.email)
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
    val imageRes: Int
)

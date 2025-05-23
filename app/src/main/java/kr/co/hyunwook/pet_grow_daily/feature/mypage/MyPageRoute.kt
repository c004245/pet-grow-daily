package kr.co.hyunwook.pet_grow_daily.feature.mypage

import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MyPageRoute(
    paddingValues: PaddingValues,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {

    }

    MyPageScreen(
        paddingValues = paddingValues,
    )
}

@Composable
fun MyPageScreen(
    paddingValues: PaddingValues,
) {

    Box(
        modifier = Modifier.fillMaxSize().background(grayF8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CommonTopBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(
                            R.string.text_mypage_title
                        ),
                        style = PetgrowTheme.typography.bold,
                        color = black21
                    )
                }
            )
            Spacer(Modifier.height(16.dp))
            MyProfileInfo()
            Spacer(Modifier.height(12.dp))
            AlarmInfo()
            Spacer(Modifier.height(12.dp))
            LegalInfo()
            Spacer(Modifier.height(12.dp))
            LogoutWidget()
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),

                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text(
                    text = "앱 버전 1.0.0",
                    style = PetgrowTheme.typography.regular,
                    color = gray5E,
                    fontSize = 12.sp,
                )
            }



        }
    }
}

@Composable
fun MyProfileInfo() {
    CommonRoundedBox {
        Column  {
            Text(
                text = "조현욱님, 안녕하세요.",
                style = PetgrowTheme.typography.medium,
                fontSize = 16.sp,
                color = black21
            )
            Text(
                text = "c004112@gmail.com",
                style = PetgrowTheme.typography.regular,
                fontSize = 13.sp,
                color = gray86

            )
        }
    }

}

@Composable
fun AlarmInfo() {
    CommonRoundedBox {
       Row(
           modifier = Modifier.fillMaxWidth(),
           horizontalArrangement = Arrangement.SpaceBetween
       ) {
            Text(
                text = stringResource(R.string.text_mypage_alarm_title),
                style = PetgrowTheme.typography.regular,
                color = black21,
                fontSize = 16.sp
            )
           Image(
               painter = painterResource(id = R.drawable.ic_left_arrow),
               contentDescription = "alarm"
           )
       }
    }

}

@Composable
fun LegalInfo() {
    CommonRoundedBox {
        Column {

            Text(
                text = stringResource(R.string.text_legal_info_title),
                style = PetgrowTheme.typography.medium,
                fontSize = 13.sp,
                color = black21
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.text_use_term_title),
                    style = PetgrowTheme.typography.regular,
                    color = black21,
                    fontSize = 16.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = "term"
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.text_privacy_title),
                    style = PetgrowTheme.typography.regular,
                    color = black21,
                    fontSize = 16.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = "privacy"
                )
            }
        }
    }
}

@Composable
fun LogoutWidget() {
    CommonRoundedBox {
        Column {
            Text(
                text = stringResource(R.string.text_logout),
                style = PetgrowTheme.typography.regular,
                color = black21,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun CommonRoundedBox(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
            spotColor = Color.White.copy(alpha = 0.05f)
        ).fillMaxWidth().padding(horizontal = 16.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

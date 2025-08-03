package kr.co.hyunwook.pet_grow_daily.feature.mypage

import android.content.Intent
import android.net.Uri
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.core.database.entity.PetProfile
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun MyPageRoute(
    viewModel: MyPageViewModel = hiltViewModel(),
    onClickDeliveryList: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickBusinessInfo: () -> Unit
) {
    val context = LocalContext.current
    val petProfile by viewModel.petProfile.collectAsState()
    val userInfo by viewModel.userInfo.collectAsState()

    // 갤러리 런처
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfileImage(it.toString())
        }
    }

    LaunchedEffect(Unit) {

    }

    MyPageScreen(
        petProfile = petProfile,
        userInfo = userInfo,
        onCameraClick = {
            galleryLauncher.launch("image/*")
        },
        onClickService = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.instagram.com/dailydog_around/")
            }
            context.startActivity(intent)
        },
        onClickPrivacy = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://gusty-lobe-f9b.notion.site/22ce91dcf9a180fbbd18d5bb86cf2988?source=copy_link")
            }
            context.startActivity(intent)
        },
        onClickTerm = {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data =
                    Uri.parse("https://gusty-lobe-f9b.notion.site/22ce91dcf9a180a8943eea44d6d1d4e6?source=copy_link")
            }
            context.startActivity(intent)
        },
        onClickAlarm = {
            onClickAlarm()


        },
        onClickDeliveryList = {
            Log.d("HWO", "onc!")
            onClickDeliveryList()
        },
        onClickBusinessInfo = {
            onClickBusinessInfo()
        }

    )
}

@Composable
fun MyPageScreen(
    petProfile: PetProfile?,
    userInfo: Pair<String?, String?>,
    onCameraClick: () -> Unit,
    onClickService: () -> Unit,
    onClickTerm: () -> Unit,
    onClickPrivacy: () -> Unit,
    onClickAlarm: () -> Unit,
    onClickDeliveryList: () -> Unit,
    onClickBusinessInfo: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(grayF8)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
            MyProfileInfo(petProfile, userInfo, onCameraClick)
            Spacer(Modifier.height(12.dp))
            AlarmInfo(onClickService, onClickAlarm, onClickDeliveryList)
            Spacer(Modifier.height(12.dp))
            LegalInfo(onClickTerm, onClickPrivacy, onClickBusinessInfo)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),

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
fun MyProfileInfo(petProfile: PetProfile?,
                  userInfo: Pair<String?, String?>,
                  onCameraClick: () -> Unit) {
    CommonRoundedBox {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (petProfile != null && petProfile.profileImageUrl.isNotEmpty()) {
                    @OptIn(ExperimentalGlideComposeApi::class)
                    GlideImage(
                        model = petProfile.profileImageUrl,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        loading = placeholder(ColorPainter(Color.Gray.copy(alpha = 0.3f))),
                        failure = placeholder(ColorPainter(Color.Red.copy(alpha = 0.3f))),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 더미 프로필 이미지
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(gray86),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = petProfile?.name?.firstOrNull()?.toString() ?: "?",
                            style = PetgrowTheme.typography.bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    }
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_camera_add),
                    contentDescription = "camera",
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(20.dp)
                        .clickable { onCameraClick() }
                )
            }

            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "${userInfo.first ?: "사용자"}님, 안녕하세요.",
                    style = PetgrowTheme.typography.medium,
                    fontSize = 16.sp,
                    color = black21
                )
                Text(
                    text = "${userInfo.second}",
                    style = PetgrowTheme.typography.regular,
                    fontSize = 13.sp,
                    color = gray86
                )
            }
        }
    }
}

@Composable
fun AlarmInfo(
    onClickService: () -> Unit, onClickAlarm: () -> Unit, onClickDelivery: () -> Unit
) {
    CommonRoundedBox(
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickService()
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.text_service_setting_title),
                    style = PetgrowTheme.typography.regular,
                    color = black21,
                    fontSize = 16.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = "delivery"
                )
            }
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        Log.d("HWO", "onCLickDele")
                        onClickDelivery()
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.text_delivery_setting_title),
                    style = PetgrowTheme.typography.regular,
                    color = black21,
                    fontSize = 16.sp
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_left_arrow),
                    contentDescription = "delivery"
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickAlarm()
                    },
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

}

@Composable
fun LegalInfo(onClickTerm: () -> Unit, onClickPrivacy: () -> Unit, onClickBusiness: () -> Unit){
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickTerm()
                    },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickPrivacy()
                    },
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
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onClickBusiness()
                    },
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.text_business_info),
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
fun LogoutWidget(onClickLogout: () -> Unit) {
    CommonRoundedBox(
        onClick = onClickLogout
    ) {
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
    onClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = Color(0x0D000000), // #0000000D
                ambientColor = Color(0x0D000000)
            )
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

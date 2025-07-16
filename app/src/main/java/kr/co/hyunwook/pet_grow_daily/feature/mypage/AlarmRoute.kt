package kr.co.hyunwook.pet_grow_daily.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.delivery.TitleDeliveryAppBar
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun AlarmRoute(
    navigateToMyPage: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AlarmViewModel = hiltViewModel()
) {
    val photoReminderEnabled by viewModel.photoReminderEnabled.collectAsState()
    val deliveryNotificationEnabled by viewModel.deliveryNotificationEnabled.collectAsState()
    val marketingNotificationEnabled by viewModel.marketingNotificationEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    AlarmScreen(
        navigateToMyPage = navigateToMyPage,
        modifier = modifier,
        photoReminderEnabled = photoReminderEnabled,
        deliveryNotificationEnabled = deliveryNotificationEnabled,
        marketingNotificationEnabled = marketingNotificationEnabled,
        isLoading = isLoading,
        onPhotoReminderToggle = { enabled ->
            viewModel.setPhotoReminderEnabled(enabled)
        },
        onDeliveryNotificationToggle = { enabled ->
            viewModel.setDeliveryNotificationEnabled(enabled)
        },
        onMarketingNotificationToggle = { enabled ->
            viewModel.setMarketingNotificationEnabled(enabled)
        }
    )
}

@Composable
fun AlarmScreen(
    navigateToMyPage: () -> Unit = {},
    modifier: Modifier = Modifier,
    photoReminderEnabled: Boolean = true,
    deliveryNotificationEnabled: Boolean = true,
    marketingNotificationEnabled: Boolean = true,
    isLoading: Boolean = false,
    onPhotoReminderToggle: (Boolean) -> Unit = {},
    onDeliveryNotificationToggle: (Boolean) -> Unit = {},
    onMarketingNotificationToggle: (Boolean) -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(grayF8)
            .padding(24.dp)
    ) {
        TitleDeliveryAppBar(
            title = stringResource(R.string.text_mypage_alarm_title),
            navigateToBack = {
                navigateToMyPage()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 사진 등록 리마인드
        AlarmToggleItem(
            title = "사진 등록 리마인드 📸",
            isEnabled = photoReminderEnabled,
            onToggle = onPhotoReminderToggle,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 제작/배송 알림
        AlarmToggleItem(
            title = "제작/배송 알림 📦",
            isEnabled = deliveryNotificationEnabled,
            onToggle = onDeliveryNotificationToggle,
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        AlarmToggleItem(
            title = "마케팅 알림 🎉",
            isEnabled = marketingNotificationEnabled,
            onToggle = onMarketingNotificationToggle,
            isLoading = isLoading
        )
    }
}

@Composable
fun AlarmToggleItem(
    title: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = PetgrowTheme.typography.regular,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            enabled = !isLoading,
            colors = SwitchDefaults.colors(
                checkedThumbColor = purple6C,
                checkedTrackColor = purple6C.copy(alpha = 0.3f),
                uncheckedThumbColor = gray86,
                uncheckedTrackColor = gray86.copy(alpha = 0.3f)
            )
        )
    }
}

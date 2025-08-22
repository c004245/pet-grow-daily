package kr.co.hyunwook.pet_grow_daily

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.messaging.FirebaseMessaging
import kr.co.hyunwook.pet_grow_daily.feature.main.MainNavigator
import kr.co.hyunwook.pet_grow_daily.feature.main.MainScreen
import kr.co.hyunwook.pet_grow_daily.feature.main.MainViewModel
import kr.co.hyunwook.pet_grow_daily.feature.main.rememberMainNavigator
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val permissions = arrayOf(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.READ_MEDIA_IMAGES
        else
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            android.Manifest.permission.POST_NOTIFICATIONS
        else
            null
    ).filterNotNull().toTypedArray()

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
            if (allGranted) {
                showMainScreen()
            } else {
                showPermissionDeniedMessage()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { false }

        // FCM 초기화
        initializeFCM()

        if (hasAllPermissions()) {
            showMainScreen()
        } else {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    private fun initializeFCM() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // FCM 등록 토큰 가져오기
            val token = task.result
            Log.d("FCM", "FCM Registration Token: $token")

            mainViewModel.saveFcmToken(token)
        }

        // 전체 알림용 토픽에 자동 구독
        FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "all_users 토픽 구독 성공" else "all_users 토픽 구독 실패"
                Log.d("FCM", msg)
            }
    }

    private fun hasAllPermissions(): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun showPermissionDeniedMessage() {
        setContent {
            PetgrowTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        style = PetgrowTheme.typography.regular,
                        fontSize = 18.sp,
                        text = "데일리 독을 이용하기 위해서는 권한이 필요합니다.\n설정에서 권한을 허용해주세요!",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    private fun showMainScreen() {
        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            PetgrowTheme {
                CompositionLocalProvider {
                    MainScreen(navigator = navigator)
                }
            }
        }
    }
}

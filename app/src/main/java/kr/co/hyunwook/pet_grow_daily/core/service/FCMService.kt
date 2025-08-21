package kr.co.hyunwook.pet_grow_daily.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kr.co.hyunwook.pet_grow_daily.MainActivity
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.SaveFcmTokenUseCase
import javax.inject.Inject

class FCMService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "pet_grow_daily_notifications"
        private const val CHANNEL_NAME = "Pet Grow Daily Notifications"
        private const val NOTIFICATION_ID = 1

        // 알림 타입 구분
        private const val NOTIFICATION_TYPE_MARKETING = "marketing"
        private const val NOTIFICATION_TYPE_DELIVERY = "delivery"
        private const val NOTIFICATION_TYPE_SYSTEM = "system"
    }


    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface FCMServiceEntryPoint {
        fun alarmSettingsUseCase(): AlarmSettingsUseCase
        fun saveFcmTokenUseCase(): SaveFcmTokenUseCase
    }

    private val entryPoint by lazy {
        EntryPointAccessors.fromApplication(
            applicationContext,
            FCMServiceEntryPoint::class.java
        )
    }

    private val alarmSettingsUseCase by lazy {
        entryPoint.alarmSettingsUseCase()
    }

    private val saveFcmTokenUseCase by lazy {
        entryPoint.saveFcmTokenUseCase()
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token: $token")
        CoroutineScope(Dispatchers.IO).launch {
            saveFcmTokenUseCase(token)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // 데이터 페이로드 처리
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        // 알림 타입 확인 (서버에서 보내는 데이터에 type 필드 포함)
        val notificationType = remoteMessage.data["type"]?.also {
            Log.d(TAG, "알림 타입(type 필드): $it")
        } ?: run {
            Log.w(
                TAG,
                "type 필드가 없어 기본값($NOTIFICATION_TYPE_SYSTEM)으로 처리됩니다. [푸쉬 메시지 작성 시 data에 type 필수!]"
            )
            NOTIFICATION_TYPE_SYSTEM
        }
        Log.d(TAG, "실제 사용 알림 타입: $notificationType")

        // 알림 또는 데이터에서 제목과 본문 추출
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"]
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"]

        Log.d(TAG, "Message Title: $title, Body: $body")

        // 알림 설정 확인 후 전송
        CoroutineScope(Dispatchers.IO).launch {
            if (shouldShowNotification(notificationType)) {
                sendNotification(title, body)
            } else {
                Log.d(TAG, "알림 설정으로 인해 알림 차단: $notificationType")
            }
        }
    }

    private suspend fun shouldShowNotification(type: String): Boolean {
        return try {
            when (type) {
                NOTIFICATION_TYPE_MARKETING -> {
                    val isEnabled = alarmSettingsUseCase.isMarketingNotificationEnabled().first()
                    Log.d(TAG, "마케팅 알림 설정: $isEnabled")
                    isEnabled
                }

                NOTIFICATION_TYPE_DELIVERY -> {
                    val isEnabled = alarmSettingsUseCase.isDeliveryNotificationEnabled().first()
                    Log.d(TAG, "배송 알림 설정: $isEnabled")
                    isEnabled
                }

                NOTIFICATION_TYPE_SYSTEM -> {
                    // 시스템 알림은 항상 표시
                    Log.d(TAG, "시스템 알림 - 항상 표시")
                    true
                }

                else -> {
                    // 알 수 없는 타입은 기본적으로 표시
                    Log.d(TAG, "알 수 없는 알림 타입: $type - 기본 표시")
                    true
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "알림 설정 확인 중 오류 발생", e)
            // 오류 발생시 기본적으로 표시
            true
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel()

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_main_app)
            .setContentTitle(title ?: "Pet Grow Daily")
            .setContentText(messageBody ?: "새로운 알림이 있습니다.")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Log.d(TAG, "알림 전송 완료: $title")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Pet Grow Daily push notifications"

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
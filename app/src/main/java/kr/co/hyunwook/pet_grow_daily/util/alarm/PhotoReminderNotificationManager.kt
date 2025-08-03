package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kr.co.hyunwook.pet_grow_daily.MainActivity
import kr.co.hyunwook.pet_grow_daily.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoReminderNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val CHANNEL_ID = "photo_reminder_channel"
    private val ORDER_CHANNEL_ID = "order_completion_channel"
    private val NOTIFICATION_ID = 1001
    private val ORDER_NOTIFICATION_ID = 1002

    init {
        createNotificationChannel()
        createOrderNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "사진 등록 리마인드",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "매일 사진 등록을 알려주는 알림"
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createOrderNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORDER_CHANNEL_ID,
                "주문 완료 알림",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "주문이 완료되었음을 알려주는 알림"
                enableLights(true)
                lightColor = Color.GREEN
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 100, 200)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showPhotoReminderNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_main_app) // 앱 아이콘
            .setContentTitle("오늘 사진을 올려 주세요! 📸")
            .setContentText("앨범이 얼마 남지 않았어요!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("소중한 반려 동물의 사진을 저장해보세요!\n곧 앨범을 제작할 수 있어요!")
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(NOTIFICATION_ID, notification)
            }
        }
    }

    fun showOrderCompletionNotification() {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ORDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_main_app)
            .setContentTitle("앨범 주문이 정상적으로 확인됐어요! 🎉")
            .setContentText("제가 이제 제작을 시작하면 다시한번 알림을 보내드릴게요! 정성들여서 만들어볼게요!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("제가 이제 제작을 시작하면 다시한번 알림을 보내드릴게요! 정성들여서 만들어볼게요!")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notify(ORDER_NOTIFICATION_ID, notification)
            }
        }
    }
}

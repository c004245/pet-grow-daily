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
import android.util.Log
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
    private val NOTIFICATION_ID = 1001

    init {
        createNotificationChannel()
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
            .setContentTitle("오늘 사진 등록을 놓치신거 같아요 📸")
            .setContentText("앨범 제작에 필요한 사진을 올려주세요!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("소중한 반려동물의 하루를 기록해보세요.\n매일 사진을 등록하면 더 많은 추억을 만들 수 있어요!")
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
                Log.d("PhotoReminderNotificationManager", "알림 전송 완료")
            } else {
                Log.w("PhotoReminderNotificationManager", "알림 권한이 없습니다")
            }
        }
    }
}


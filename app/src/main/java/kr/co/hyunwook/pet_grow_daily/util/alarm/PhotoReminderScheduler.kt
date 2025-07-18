package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

//사진 등록 리마인더 스케줄러
@Singleton
class PhotoReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val alarmSettingsUseCase: AlarmSettingsUseCase
) {
    private val workManager = WorkManager.getInstance(context)

    companion object {
        private const val WORK_NAME = "photo_reminder_work"
        private const val REMINDER_HOUR = 21
        private const val REMINDER_MINUTE = 0
    }

    suspend fun schedulePhotoReminder() {
        try {
            val isEnabled = alarmSettingsUseCase.isPhotoReminderEnabled().first()
            if (!isEnabled) {
                cancelPhotoReminder()
                return
            }

            // 매일 밤 9시에 실행되도록 설정
            val currentTime = System.currentTimeMillis()
            val calendar = Calendar.getInstance().apply {
                timeInMillis = currentTime
                set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
                set(Calendar.MINUTE, REMINDER_MINUTE)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (calendar.timeInMillis <= currentTime) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            val initialDelay = calendar.timeInMillis - currentTime
            val dailyWorkRequest = PeriodicWorkRequestBuilder<PhotoReminderWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .setRequiresBatteryNotLow(false)
                        .setRequiresCharging(false)
                        .setRequiresDeviceIdle(false)
                        .setRequiresStorageNotLow(false)
                        .build()
                )
                .addTag("photo_reminder")
                .build()

            workManager.enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
            )

        } catch (e: Exception) {
            Log.e("PhotoReminderScheduler", "사진 리마인더 스케줄링 실패", e)
        }
    }

    fun cancelPhotoReminder() {
        try {
            workManager.cancelUniqueWork(WORK_NAME)
        } catch (e: Exception) {
            Log.e("PhotoReminderScheduler", "사진 리마인더 스케줄링 취소 실패", e)
        }
    }


    // 테스트용 즉시 실행 메서드
    fun scheduleImmediateTest() {
        val testWorkRequest = OneTimeWorkRequestBuilder<PhotoReminderWorker>()
            .setInitialDelay(5, TimeUnit.SECONDS)
            .addTag("photo_reminder_test")
            .build()

        workManager.enqueue(testWorkRequest)
        Log.d("PhotoReminderScheduler", "테스트 알림 5초 후 실행")
    }
}
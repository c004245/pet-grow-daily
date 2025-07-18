package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoReminderWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PhotoReminderWorkerEntryPoint {
        fun alarmSettingsUseCase(): AlarmSettingsUseCase
        fun notificationManager(): PhotoReminderNotificationManager
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        PhotoReminderWorkerEntryPoint::class.java
    )

    private val alarmSettingsUseCase = entryPoint.alarmSettingsUseCase()
    private val notificationManager = entryPoint.notificationManager()

    override suspend fun doWork(): Result {
        return try {

            val isEnabled = alarmSettingsUseCase.isPhotoReminderEnabled().first()
            if (!isEnabled) {
                return Result.success()
            }

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastPhotoDate = alarmSettingsUseCase.getLastPhotoDate().first()


            if (lastPhotoDate != today) {
                notificationManager.showPhotoReminderNotification()
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
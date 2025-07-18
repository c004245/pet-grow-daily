package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kr.co.hyunwook.pet_grow_daily.core.domain.usecase.AlarmSettingsUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class PhotoReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmSettingsUseCase: AlarmSettingsUseCase,
    private val notificationManager: PhotoReminderNotificationManager
): CoroutineWorker(context, workerParams) {

    @AssistedFactory
    interface Factory {
        fun create(context: Context, workerParams: WorkerParameters): PhotoReminderWorker
    }


    override suspend fun doWork(): Result {
        return try {
            Log.d("PhotoReminderWorker", "사진 리마인더 작업 시작")

            // 1. 알림 설정이 켜져있는지 확인
            val isEnabled = alarmSettingsUseCase.isPhotoReminderEnabled().first()
            if (!isEnabled) {
                Log.d("PhotoReminderWorker", "사진 리마인더가 비활성화되어 있습니다")
                return Result.success()
            }
            // 2. 오늘 사진을 등록했는지 확인
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val lastPhotoDate = alarmSettingsUseCase.getLastPhotoDate().first()

            Log.d("PhotoReminderWorker", "오늘 날짜: $today, 마지막 사진 등록 날짜: $lastPhotoDate")

            // 3. 오늘 사진을 등록하지 않았다면 알림 발송
            if (lastPhotoDate != today) {
                Log.d("PhotoReminderWorker", "오늘 사진 미등록 - 알림 발송")
                notificationManager.showPhotoReminderNotification()
            } else {
                Log.d("PhotoReminderWorker", "오늘 사진 이미 등록됨 - 알림 생략")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("PhotoReminderWorker", "사진 리마인더 작업 실패", e)
            Result.failure()
        }
    }
}
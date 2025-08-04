package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

class OrderAvailableWorker(
    context: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface OrderAvailableWorkerEntryPoint {
        fun notificationManager(): PhotoReminderNotificationManager
    }

    private val entryPoint = EntryPointAccessors.fromApplication(
        applicationContext,
        OrderAvailableWorkerEntryPoint::class.java
    )

    private val notificationManager = entryPoint.notificationManager()

    override suspend fun doWork(): Result {
        return try {
            Log.d("OrderAvailableWorker", "주문 가능 알림 발송 시작")
            notificationManager.showOrderAvailableNotification()
            Log.d("OrderAvailableWorker", "주문 가능 알림 발송 완료")
            Result.success()
        } catch (e: Exception) {
            Log.e("OrderAvailableWorker", "주문 가능 알림 발송 실패", e)
            Result.failure()
        }
    }
}
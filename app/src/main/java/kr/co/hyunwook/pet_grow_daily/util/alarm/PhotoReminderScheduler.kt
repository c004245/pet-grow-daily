package kr.co.hyunwook.pet_grow_daily.util.alarm

import android.content.Context
import android.icu.util.Calendar
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

//사진 등록 리마인더 스케줄러
@Singleton
class PhotoReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedulerPhotoReminder() {
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 21)

        }
    }
}
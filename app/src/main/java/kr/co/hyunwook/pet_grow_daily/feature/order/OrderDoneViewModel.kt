package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kr.co.hyunwook.pet_grow_daily.util.alarm.PhotoReminderNotificationManager
import javax.inject.Inject

@HiltViewModel
class OrderDoneViewModel @Inject constructor(
    private val photoReminderNotificationManager: PhotoReminderNotificationManager
): ViewModel() {

    fun sendOrderCompletionNotification() {
        photoReminderNotificationManager.showOrderCompletionNotification()
    }
}
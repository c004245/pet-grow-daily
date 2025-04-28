package kr.co.hyunwook.pet_grow_daily.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
 fun formatDate(timestamp: Long): String {
    val date = remember(timestamp) {
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        formatter.format(Date(timestamp))
    }
    return date
}
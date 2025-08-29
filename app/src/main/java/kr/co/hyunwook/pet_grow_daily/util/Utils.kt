package kr.co.hyunwook.pet_grow_daily.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kr.co.hyunwook.pet_grow_daily.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun formatDate(timestamp: Long): String {
    val date = remember(timestamp) {
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        formatter.format(Date(timestamp))
    }
    return date
}

fun formatPrice(price: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return "${formatter.format(price)}원"
}

@Composable
fun CommonAppBarOnlyButton(
    navigateToBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(start = 24.dp, top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "ic_back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .clickable { navigateToBack() }
        )
    }
}

const val MAX_ALBUM_COUNT = 42 //앨범 제작에 필요한 카운트
const val MAX_ALBUM_INSTA_BOOK_COUNT = 62 //인스타북 제작에 필요한 카운트
const val TODAY_LIMIT_CREATE = 5 //하루 앨범 제작 리밋
const val ORDER_TODAY_DONE = 0
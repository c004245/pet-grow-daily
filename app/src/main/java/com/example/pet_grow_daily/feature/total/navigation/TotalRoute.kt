package com.example.pet_grow_daily.feature.total.navigation

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun TotalRoute(
    paddingValues: PaddingValues
) {
    TotalScreen(
        paddingValues = paddingValues
    )
}

@Composable
fun TotalScreen(
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
    ) {
        CommonTopBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.text_total_title),
                    style = PetgrowTheme.typography.bold
                )
            }
        )
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            TotalMonthly()
            TotalSummary()
            TotalCategory()
            TotalGrowItem()
        }

    }
}

@Composable
fun TotalMonthly() {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_total_left_arrow),
            contentDescription = "total_left_arrow",
            modifier = Modifier.clickable {
                Log.d("HWO", "left arrow")
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = "6월",
            fontSize = 21.sp,
            style = PetgrowTheme.typography.bold,
            color = black21
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_total_right_arrow),
            contentDescription = "total_right_arrow",
            modifier = Modifier.clickable {
                Log.d("HWO", "right arrow")
            }
        )
    }
}

@Composable
fun TotalSummary() {
    
}

@Composable
fun TotalCategory() {

}

@Composable
fun TotalGrowItem() {

}

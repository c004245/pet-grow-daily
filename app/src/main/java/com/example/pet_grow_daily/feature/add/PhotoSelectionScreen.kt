package com.example.pet_grow_daily.feature.add

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayf1
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.feature.home.EmptyTodayGrowRecordWidget
import com.example.pet_grow_daily.ui.theme.PetgrowTheme


@Composable
fun PhotoSelectionScreen(onPhotoSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            EmptyTodayGrowRecordWidget(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_background_album),
                        contentDescription = "background_album"

                    )

                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ChoosePhotoSelectWidget(onPhotoSelected)
            Spacer(modifier = Modifier.height(16.dp))
        }
//        Button(
//            onClick = { onPhotoSelected() },
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth(),
//            shape = RoundedCornerShape(8.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = purple6C
//            )
//        ) {
//            Text(
//                text = stringResource(id = R.string.text_next),
//                style = PetgrowTheme.typography.bold,
//                color = Color.White,
//                fontSize = 14.sp
//            )
//        }
    }

}

@Composable
fun ChoosePhotoSelectWidget(onPhotoSelected: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clickable {
                    Log.d("HWO", "clicked")
                }
                .weight(1f)
                .height(56.dp)
                .background(color = grayf1, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_picture_camera),
                    contentDescription = "camera"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = stringResource(id = R.string.text_picture),
                    fontSize = 12.sp,
                    style = PetgrowTheme.typography.bold,
                    color = black21
                )
            }

        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clickable {

                }
                .weight(1f)
                .height(56.dp)
                .background(color = grayf1, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_picture_album),
                    contentDescription = "camera"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    modifier = Modifier.wrapContentWidth(),
                    text = stringResource(id = R.string.text_gallery),
                    fontSize = 12.sp,
                    style = PetgrowTheme.typography.bold,
                    color = black21
                )
            }

        }
    }
}
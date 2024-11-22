package com.example.pet_grow_daily.feature.total.navigation

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayf1
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.feature.add.CategoryType
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
    val items = listOf("전체", "간식", "물 마시기", "약 먹기", "목욕", "산책", "운동", "학습", "놀이", "식사", "양치", "잠자기")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
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
            TotalMonthly(
                onPreviousMonth = {

                },
                onNextMonth = {

                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            TotalSummary()
            Spacer(modifier = Modifier.height(16.dp))
            TotalCategory(items = items,
                onCategorySelected = {

                })
        }

        Spacer(modifier = Modifier.height(16.dp))
        TotalGrowItem()

    }

}
}

@Composable
fun TotalMonthly(
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_total_left_arrow),
            contentDescription = "total_left_arrow",
            modifier = Modifier.clickable {
                onPreviousMonth()
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
                onNextMonth()
            }
        )
    }
}

@Composable
fun TotalSummary() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = purple6C, shape = RoundedCornerShape(16.dp)),
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = "건빵이의 6월 활동을 알려드릴게요!",
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_medicine_select),
                    contentDescription = "medicine"
                )
                Text(
                    text = "0",
                    modifier = Modifier.padding(start = 4.dp),
                    style = PetgrowTheme.typography.medium,
                    color = Color.White,
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_medicine_select),
                    contentDescription = "medicine"
                )
                Text(
                    text = "0",
                    modifier = Modifier.padding(start = 4.dp),
                    style = PetgrowTheme.typography.medium,
                    color = Color.White,
                    fontSize = 12.sp,
                )

                Spacer(modifier = Modifier.width(24.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_medicine_select),
                    contentDescription = "medicine"
                )
                Text(
                    text = "0",
                    modifier = Modifier.padding(start = 4.dp),
                    style = PetgrowTheme.typography.medium,
                    color = Color.White,
                    fontSize = 12.sp,
                )


            }
        }

    }
}

@Composable
fun TotalCategory(
    items: List<String>,
    onCategorySelected: (String) -> Unit
) {
    var selectedItem by remember { mutableStateOf(items.first()) }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) { item ->
            CategoryItem(
                text = item,
                isSelected = selectedItem == item,
                onCategoryClick = {
                    selectedItem = item
                    onCategorySelected(item)
                }
            )
        }
    }
}


@Composable
fun CategoryItem(
    text: String,
    isSelected: Boolean,
    onCategoryClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (isSelected) purple6C else grayf1)
            .clickable(onClick = onCategoryClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else black21,
            fontSize = 14.sp,
            style = PetgrowTheme.typography.bold
        )
    }
}

@Composable
fun TotalGrowItem() {
    val items = listOf("전체", "간식", "물 마시기", "약 먹기", "목욕", "산책", "운동", "학습", "놀이", "식사", "양치", "잠자기")
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(items) { item ->
            GrowItem(onGrowClick = {

            }
            )
        }
    }

}

@Composable
fun GrowItem(
//    text: GrowModel,
    onGrowClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(grayf1)
            .clickable(onClick = onGrowClick)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(8.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_total_dummy),
                    contentDescription = "total_image"
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_medicine_select),
                        contentDescription = "medicine"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "간식",
                        modifier = Modifier.wrapContentWidth(),
                        style = PetgrowTheme.typography.bold,
                        color = black21,
                        fontSize = 14.sp
                    )

                }
                Text(
                    "2022.06.01(일) 14:56",
                    modifier = Modifier.wrapContentWidth(),
                    style = PetgrowTheme.typography.medium,
                    color = gray86,
                    fontSize = 12.sp

                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_total_arrow),
                contentDescription = "fullscreen",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {

                    }
            )

        }
    }

}


data class GrowModel(
    val photoUrl: String,
    val categoryType: CategoryType,
    val timeStamp: Long
)

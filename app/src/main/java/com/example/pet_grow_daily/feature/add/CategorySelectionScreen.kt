package com.example.pet_grow_daily.feature.add

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.grayAD
import com.example.pet_grow_daily.core.designsystem.theme.grayf1
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun CategorySelectionScreen(onCategorySelected: () -> Unit) {

    var currentCategory by remember { mutableStateOf(CategoryType.SNACK) }

    val categoryItems = listOf(
        CategoryItemData(
            "간식",
            R.drawable.ic_snack_select,
            R.drawable.ic_snack_unselect,
            CategoryType.SNACK
        ),
        CategoryItemData(
            "물 마시기",
            R.drawable.ic_water_select,
            R.drawable.ic_water_unselect,
            CategoryType.WATER
        ),
        CategoryItemData(
            "약 먹기",
            R.drawable.ic_medicine_select,
            R.drawable.ic_medicine_unselect,
            CategoryType.MEDICINE
        ),
        CategoryItemData(
            "목욕",
            R.drawable.ic_bath_select,
            R.drawable.ic_bath_unselect,
            CategoryType.BATH
        ),
        CategoryItemData(
            "병원",
            R.drawable.ic_hospital_select,
            R.drawable.ic_hospital_unselect,
            CategoryType.HOSPITAL
        ),
        CategoryItemData(
            "산책",
            R.drawable.ic_outwork_select,
            R.drawable.ic_outwork_unselect,
            CategoryType.OUT_WORK
        ),
        CategoryItemData(
            "수면",
            R.drawable.ic_sleep_select,
            R.drawable.ic_sleep_unselect,
            CategoryType.SLEEP
        ),
        CategoryItemData(
            "실내놀이",
            R.drawable.ic_inplay_select,
            R.drawable.ic_inplay_unselect,
            CategoryType.IN_PLAY
        ),
        CategoryItemData(
            "실외놀이",
            R.drawable.ic_outplay_select,
            R.drawable.ic_outplay_unselect,
            CategoryType.OUT_PLAY
        ),
        CategoryItemData(
            "이벤트",
            R.drawable.ic_event_select,
            R.drawable.ic_event_unselect,
            CategoryType.EVENT
        ),
        CategoryItemData(
            "기타",
            R.drawable.ic_etc_select,
            R.drawable.ic_etc_unselect,
            CategoryType.ETC
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()

        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categoryItems) { item ->
                    CategoryGridItem(
                        item = item,
                        isSelected = item.categoryType == currentCategory,
                        onCategoryClick = {
                            currentCategory =
                                if (currentCategory == item.categoryType) CategoryType.NONE else item.categoryType


                        })
                }
            }
        }
        Button(
            onClick = { onCategorySelected() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple6C
            )
        ) {
            Text(
                text = stringResource(id = R.string.text_next),
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )
        }


    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryGridItem(item: CategoryItemData, isSelected: Boolean, onCategoryClick: () -> Unit) {
    val backgroundColor = if (isSelected) purple6C else grayf1 // 선택된 경우 배경색 변경
    val shape = RoundedCornerShape(16.dp)


    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .combinedClickable(
                onClick = onCategoryClick,
                indication = LocalIndication.current,
                interactionSource = remember { MutableInteractionSource() }

            ),
        shape = shape,
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = if (isSelected) item.selectIcon else item.unselectIcon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.name,
                fontSize = 12.sp,
                style = PetgrowTheme.typography.medium,
                color = if(isSelected) Color.White else black21
            )
        }
    }
}


data class CategoryItemData(
    val name: String,
    val selectIcon: Int,
    val unselectIcon: Int,
    val categoryType: CategoryType
)

enum class CategoryType {
    SNACK,
    WATER,
    MEDICINE,
    BATH,
    HOSPITAL,
    OUT_WORK,
    SLEEP,
    IN_PLAY,
    OUT_PLAY,
    EVENT,
    ETC,
    NONE
}
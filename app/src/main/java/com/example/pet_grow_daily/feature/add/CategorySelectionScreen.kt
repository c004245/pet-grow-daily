package com.example.pet_grow_daily.feature.add

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun CategorySelectionScreen(onCategorySelected: () -> Unit) {
    val categoryItems = listOf(
        CategoryItemData("간식", R.drawable.ic_water),
        CategoryItemData("물 마시기", R.drawable.ic_water),
        CategoryItemData("약 먹기", R.drawable.ic_water),
        CategoryItemData("목욕", R.drawable.ic_water),
        CategoryItemData("병원", R.drawable.ic_water),
        CategoryItemData("산책", R.drawable.ic_water),
        CategoryItemData("수면", R.drawable.ic_water),
        CategoryItemData("실내놀이", R.drawable.ic_water),
        CategoryItemData("실외놀이", R.drawable.ic_water),
        CategoryItemData("이벤트", R.drawable.ic_water),
        CategoryItemData("기타", R.drawable.ic_water)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)

    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryItems) { item ->
                CategoryGridItem(item = item, onClick = { onCategorySelected() })
            }
        }
    }
}


@Composable
fun CategoryGridItem(item: CategoryItemData, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        color = grayf1
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.iconRes),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                colorFilter = ColorFilter.tint(Color.Black)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = item.name,
                fontSize = 12.sp,
                style = PetgrowTheme.typography.medium,
                color = black21
            )
        }
    }
}



data class CategoryItemData(val name: String, val iconRes: Int)
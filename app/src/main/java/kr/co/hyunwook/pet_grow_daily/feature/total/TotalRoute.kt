package kr.co.hyunwook.pet_grow_daily.feature.total

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.add.CategoryType
import kr.co.hyunwook.pet_grow_daily.feature.main.NavigateEnum
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.LoadGalleryImage
import kr.co.hyunwook.pet_grow_daily.util.formatTimestampToDateTime
import kr.co.hyunwook.pet_grow_daily.util.getCategoryItem
import kr.co.hyunwook.pet_grow_daily.util.getCategoryType
import kr.co.hyunwook.pet_grow_daily.util.getStringToCategoryType
import java.util.Calendar.*

val JANUARY_MONTH = 1
val DECEMBER_MONTH = 12

@Composable
fun TotalRoute(
    viewModel: TotalViewModel = hiltViewModel()
) {

    val calendar = remember { getInstance() }
    var currentMonth by remember { mutableStateOf(calendar.get(MONTH) + 1) } // 초기 월 설정
    val defaultCategory = CategoryType.ALL

    val monthlyGrowRecords by viewModel.monthlyGrowRecords.collectAsState()
    val dogName by viewModel.dogName.collectAsState()
    val topMonthlyCategories by viewModel.topCategories.collectAsState()



    LaunchedEffect(Unit) {
        viewModel.fetchDogName()
        viewModel.getMonthlyCategoryGrowRecord(defaultCategory, currentMonth.toString())
    }

    // Group state into a single object
    val totalScreenState = TotalScreenState(
        dogName = dogName,
        categories = topMonthlyCategories,
        monthlyGrowRecords = monthlyGrowRecords,
        currentMonth = currentMonth
    )

    TotalScreen(
        state = totalScreenState,
        onPreviousMonth = {
            calendar.add(MONTH, -1)
            currentMonth = calendar.get(MONTH) + 1
            viewModel.getMonthlyCategoryGrowRecord(defaultCategory, currentMonth.toString())
        },

        onNextMonth = {
            calendar.add(MONTH, 1)
            currentMonth = calendar.get(MONTH) + 1
            viewModel.getMonthlyCategoryGrowRecord(defaultCategory, currentMonth.toString())

        },
        onCategorySelect = { selectCategoryType ->
            viewModel.getMonthlyCategoryGrowRecord(selectCategoryType, currentMonth.toString())
        }
    )
}

@Composable
fun TotalScreen(
    state: TotalScreenState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onCategorySelect: (CategoryType) -> Unit
) {
    val items =
        listOf("전체", "간식", "물 마시기", "약 먹기", "목욕", "병원", "산책", "수면", "실내놀이", "실외놀이", "이벤트", "기타")

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
                currentMonth = state.currentMonth, // 현재 월 전달
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )
            Spacer(modifier = Modifier.height(8.dp))
            TotalSummary(
                dogName = state.dogName,
                categories = state.categories,
                currentMonth = state.currentMonth, // 현재 월 전달
            )
            Spacer(modifier = Modifier.height(16.dp))
            TotalCategory(items = items,
                onCategorySelected = { selectCategory ->
                    onCategorySelect(getStringToCategoryType(selectCategory))
                })
        }

        Spacer(modifier = Modifier.height(16.dp))
        TotalGrowItem(state.monthlyGrowRecords)
    }
}

@Composable
fun TotalMonthly(
    currentMonth: Int,
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
                if (currentMonth > JANUARY_MONTH) {
                    onPreviousMonth()
                }
            }
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = "${currentMonth}월",
            fontSize = 21.sp,
            style = PetgrowTheme.typography.bold,
            color = black21
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_total_right_arrow),
            contentDescription = "total_right_arrow",
            modifier = Modifier.clickable {
                if (currentMonth < DECEMBER_MONTH) {
                    onNextMonth()
                }
            }
        )
    }
}

@Composable
fun TotalSummary(
    dogName: String,
    categories: List<CategoryCount>,
    currentMonth: Int
) {
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
                text = "${dogName}에 ${currentMonth}월 활동을 알려드릴게요!",
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(
                modifier = Modifier.padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { category ->
                    CategorySummaryItem(category)
                }
            }
        }

    }
}

@Composable
fun CategorySummaryItem(category: CategoryCount) {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = getCategoryItem(category.categoryType, NavigateEnum.MYPAGE),
            contentDescription = "category image",
            tint = Color.White,
            modifier = Modifier.size(24.dp) // Adjust icon size as needed
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = category.count.toString(),
            style = PetgrowTheme.typography.medium,
            color = Color.White,
            fontSize = 12.sp,
        )
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
fun TotalGrowItem(monthlyGrowRecords: List<GrowRecord>) {

    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(monthlyGrowRecords) { model ->
            GrowItem(model, onGrowClick = {
                selectedImageUri = model.photoUrl
            })
        }
    }

    selectedImageUri?.let { uri ->
        ImageFullScreenPopup(
            imageUri = uri,
            onClose =  { selectedImageUri = null}
        )
    }
}



@Composable
fun GrowItem(
    model: GrowRecord,
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
            LoadGalleryImage(
                uri = model.photoUrl,
                modifier = Modifier
                    .size(39.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = getCategoryItem(model.categoryType, NavigateEnum.MYPAGE),
                        contentDescription = "medicine"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        getCategoryType(model.categoryType),
                        modifier = Modifier.wrapContentWidth(),
                        style = PetgrowTheme.typography.bold,
                        color = black21,
                        fontSize = 14.sp
                    )

                }
                Text(
                    formatTimestampToDateTime(model.timeStamp),
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
            )

        }
    }

}


@Composable
fun ImageFullScreenPopup(imageUri: String, onClose: () -> Unit) {
    Dialog(onDismissRequest = onClose) { // 팝업 창
        Box(
            modifier = Modifier
                .background(purple6C)
                .clickable { onClose() }, // 클릭하면 닫기
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 이미지 로드
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)) // 배경 모서리 둥글게
                        .background(purple6C) // 보라색 배경
                ) {
                    // 이미지
                    LoadGalleryImage(
                        uri = imageUri,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp)) // 이미지 모서리 반경
                            .background(Color.White) // 이미지 배경 흰색으로 설정 (시각적 구분)
                    )
                }
            }
        }
    }
}


package com.example.pet_grow_daily.feature.add

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.database.entity.GrowRecord
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.grayAD
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun BottomSheetContent(onCloseClick: (GrowRecord) -> Unit) {
    var currentStep by remember { mutableStateOf(Step.PHOTO) }
    var photoUrl by remember { mutableStateOf("") }
    var categoryType by remember { mutableStateOf(CategoryType.SNACK) }
    var emotionType by remember { mutableStateOf(EmotionType.HAPPY) }
    var memo by remember { mutableStateOf("") }

    val sheetHeight = 580.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(sheetHeight)
            .padding(horizontal = 16.dp)
    ) {
        AddGrowAppBar(currentStep,
            onClickPhoto = { currentStep = Step.PHOTO },
            onClickCategory = { currentStep = Step.CATEGORY },
            onClickEmotion = { currentStep = Step.EMOTION }
        )
        Spacer(modifier = Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
        ) {
            when (currentStep) {
                Step.PHOTO -> PhotoSelectionScreen(
                    onPhotoSelected = { selectPhotoUrl ->
                        Log.d("HWO", "Photo Url -> $photoUrl")
                        photoUrl = selectPhotoUrl
                        currentStep = Step.CATEGORY
                    }
                )

                Step.CATEGORY -> CategorySelectionScreen(
                    onCategorySelected = { selectCategoryType ->
                        Log.d("HWO", "Category Type -> $selectCategoryType")
                        categoryType = selectCategoryType
                        currentStep = Step.EMOTION
                    }
                )

                Step.EMOTION -> EmotionSelectionScreen(
                    onEmotionSelected = { selectEmotionType, selectMemo ->
                        Log.d("HWO", "Emotion Type -> $selectEmotionType -- $selectMemo")
                        emotionType = selectEmotionType
                        memo = selectMemo

                        val record = GrowRecord(
                            photoUrl = photoUrl,
                            categoryType = categoryType,
                            emotionType = emotionType,
                            memo = memo,
                            timeStamp = System.currentTimeMillis()
                        )
                        onCloseClick(record)
                    }
                )

            }

        }
    }
}


@Composable
fun AddGrowAppBar(
    currentStep: Step,
    onClickPhoto: () -> Unit,
    onClickCategory: () -> Unit,
    onClickEmotion: () -> Unit
) {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onClickPhoto()
                },
            text = stringResource(id = R.string.text_picture_add),
            style = PetgrowTheme.typography.bold,
            color = if (currentStep == Step.PHOTO) black21 else grayAD,

            )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = "arrow"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onClickCategory()
                },
            text = stringResource(id = R.string.text_category_add),
            style = PetgrowTheme.typography.bold,
            color = if (currentStep == Step.CATEGORY) black21 else grayAD

        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = "arrow"
        )
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .clickable {
                    onClickEmotion()
                },
            text = stringResource(id = R.string.text_emotion_add),
            style = PetgrowTheme.typography.bold,
            color = if (currentStep == Step.EMOTION) black21 else grayAD
        )
    }
}


enum class Step {
    PHOTO,
    CATEGORY,
    EMOTION
}
package com.example.pet_grow_daily.feature.add

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.sp
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.grayAD
import com.example.pet_grow_daily.core.designsystem.theme.grayDE
import com.example.pet_grow_daily.core.designsystem.theme.grayf1
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun EmotionSelectionScreen(onEmotionSelected: () -> Unit) {

    var currentEmotion by remember { mutableStateOf(EmotionType.HAPPY) }
    val emotionItems = listOf(
        EmotionItemData(R.drawable.ic_happy_select, R.drawable.ic_happy_unselect, EmotionType.HAPPY),
        EmotionItemData(R.drawable.ic_sad_select, R.drawable.ic_sad_unselect, EmotionType.SAD),
        EmotionItemData(R.drawable.ic_sick_select, R.drawable.ic_sick_unselect, EmotionType.SICK),
        EmotionItemData(R.drawable.ic_good_select, R.drawable.ic_good_unselect, EmotionType.GOOD),
        EmotionItemData(R.drawable.ic_love_select, R.drawable.ic_love_unselect, EmotionType.LOVE),
        EmotionItemData(R.drawable.ic_smile_select, R.drawable.ic_smile_unselect, EmotionType.SMILE),
        EmotionItemData(R.drawable.ic_cry_select, R.drawable.ic_cry_unselect, EmotionType.CRY),
        EmotionItemData(R.drawable.ic_angry_select, R.drawable.ic_angry_unselect, EmotionType.ANGRY)
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
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(emotionItems) { item ->
                    EmotionGridItem(item = item,
                    isSelected = item.emotionType == currentEmotion,
                        onClick = {
                            currentEmotion = if (currentEmotion == item.emotionType) EmotionType.NONE else item.emotionType // 선택 상태 토글



                    })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            MemoTextField()
        }

        Button(
            onClick = {},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = purple6C
            )
        ) {
            Text(
                text = stringResource(id = R.string.text_record_save),
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )
        }


    }
}

@Composable
fun MemoTextField() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(139.dp)
            .border(BorderStroke(1.dp, grayDE), shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp)
    ) {
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("메모(선택)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent, // 내부 테두리 제거
                unfocusedBorderColor = Color.Transparent, // 내부 테두리 제거
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray
            ),
        )

    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EmotionGridItem(item: EmotionItemData, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) purple6C else grayf1 // 선택된 경우 배경색 변경
    val shape = RoundedCornerShape(16.dp)

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(shape)
            .combinedClickable(
                onClick = onClick,
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
                painter = painterResource(id = if (isSelected) item.selectIcon else  item.unselectIcon),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

data class EmotionItemData(val selectIcon: Int, val unselectIcon: Int, val emotionType: EmotionType)

enum class EmotionType {
    HAPPY,
    SAD,
    SICK,
    GOOD,
    LOVE,
    SMILE,
    CRY,
    ANGRY,
    NONE

}
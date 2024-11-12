package com.example.pet_grow_daily.feature.add

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
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.feature.home.EmptyTodayGrowRecordWidget
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun BottomSheetContent(onCloseClick: () -> Unit) {
    var currentStep by remember { mutableStateOf(Step.PHOTO) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        AddGrowAppBar()
        Spacer(modifier = Modifier.height(24.dp))
        when (currentStep) {
            Step.PHOTO ->
        }

    }
}

/**
 * EmptyTodayGrowRecordWidget(
 *             modifier = Modifier.padding(top = 24.dp)
 *         ) {
 *             Box(
 *                 modifier = Modifier.fillMaxSize(),
 *                 contentAlignment = Alignment.Center
 *             ) {
 *                 Icon(
 *                     painter = painterResource(id = R.drawable.ic_background_album),
 *                     contentDescription = "background_album"
 *                 )
 *
 *             }
 *         }
 *
 */
@Composable
fun AddGrowAppBar() {
    Row(
        modifier = Modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = stringResource(id = R.string.text_picture_add),
            style = PetgrowTheme.typography.bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = "arrow"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = stringResource(id = R.string.text_category_add),
            style = PetgrowTheme.typography.bold
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            painter = painterResource(id = R.drawable.ic_left_arrow),
            contentDescription = "arrow"
        )
        Text(
            modifier = Modifier.wrapContentWidth(),
            text = stringResource(id = R.string.text_emotion_add),
            style = PetgrowTheme.typography.bold
        )
    }
}

@Composable
fun PhotoSelectionScreen(onPhotoSelected: () -> Unit) {

}

@Composable
fun CategorySelectionScreen(onCategorySelected: () -> Unit) {
    
}
)
enum class Step {
    PHOTO,
    CATEGORY,
    EMOTION
}
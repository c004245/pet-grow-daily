package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleC4
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileNamePage(
    nameText: String,
    onNameChange: (String) -> Unit,
    onNextClick: () -> Unit
) {
    val insets = WindowInsets.ime
    val keyboardHeight = with(LocalDensity.current) { insets.getBottom(LocalDensity.current).toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = if (keyboardHeight > 0.dp) keyboardHeight else 16.dp) // 키보드 높이에 따라 버튼 이동
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 100.dp, start = 8.dp),
                text = stringResource(id = R.string.text_name_string),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            DogNameTextField(text = nameText,
                onTextChange = onNameChange )


        }
        Button(
            onClick = {
                if (nameText.isNotEmpty()) onNextClick()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(
                    PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    )
                ),

            shape = RoundedCornerShape(8.dp),
            colors = if (nameText.isNotEmpty()) {
                ButtonDefaults.buttonColors(
                    containerColor = purple6C
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = purpleC4
                )
            }
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



@Composable
fun DogNameTextField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(BorderStroke(1.dp, grayDE), shape = RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                color = black21,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(black21),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "반려견 이름",
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}

package com.example.pet_grow_daily.feature.main.onboarding

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.grayDE
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.core.designsystem.theme.purpleC4
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun OnBoardingScreen(
    navigateToName: () -> Unit = {},
) {

    val context = LocalContext.current

    val view = LocalView.current

    val density = LocalDensity.current

    var nameText by remember {
        mutableStateOf("")
    }




    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(id = R.string.text_onboarding_title),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.text_onboarding_description),
                style = PetgrowTheme.typography.light,
                color = black21,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(40.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_onboarding_background),
                contentDescription = "background"
            )
        }
//        DogNameTextField(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
//            text = nameText,
//            onTextChange = { nameText = it })


        Button(
            onClick = {
                navigateToName()
//                if (nameText.isNotEmpty()) viewModel.saveDogName(nameText)
//                 viewModel.saveDogName("군빵")
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
//            colors = if (nameText.isNotEmpty()) {
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple6C
                )
//            } else {
//                ButtonDefaults.buttonColors(
//                    containerColor = purpleC4
//                )
//            }
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
//@Composable
//fun DogNameTextField(
//    modifier: Modifier = Modifier,
//    text: String,
//    onTextChange: (String) -> Unit
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .border(BorderStroke(1.dp, grayDE), shape = RoundedCornerShape(8.dp))
//            .padding(horizontal = 16.dp), // 외부 여백
//        contentAlignment = Alignment.Center
//    ) {
//        OutlinedTextField(
//            value = text,
//            onValueChange = onTextChange,
//            label = { Text("반려견 이름") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp), // 명시적 높이 설정
//            colors = OutlinedTextFieldDefaults.colors(
//                focusedBorderColor = Color.Transparent, // 내부 테두리 제거
//                unfocusedBorderColor = Color.Transparent, // 내부 테두리 제거
//                cursorColor = Color.Black,
//                focusedTextColor = Color.Black,
//                unfocusedTextColor = Color.Black,
//                disabledTextColor = Color.Gray
//            ),
//            textStyle = TextStyle(
//                textAlign = TextAlign.Start, // 텍스트의 정렬
//                fontSize = 16.sp, // 글자 크기
//                lineHeight = 24.sp // 줄 간격을 조정하여 중앙 정렬 느낌 보정
//            ),
//            singleLine = true,
//            shape = RoundedCornerShape(8.dp) // 모서리 둥글기 설정
//        )
//    }
//}


@Preview
@Composable
fun OnBoardingScreenPreview() {
    OnBoardingScreen()
}
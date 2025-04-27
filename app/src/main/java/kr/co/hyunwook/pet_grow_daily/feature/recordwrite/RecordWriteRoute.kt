package kr.co.hyunwook.pet_grow_daily.feature.recordwrite

import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray60
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.add.AddViewModel
import kr.co.hyunwook.pet_grow_daily.feature.add.MemoTextField
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RecordWriteRoute(
    viewModel: AddViewModel = hiltViewModel(),
    navigateToAlbum: () -> Unit
) {
    RecordWriteScreen(
        navigateToAlbum = navigateToAlbum
    )


}

@Composable
fun RecordWriteScreen(
    navigateToAlbum: () -> Unit
) {

    var memoText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().background(grayF8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TitleAppBar()
            RecordWriteContentCard(
                memoText = memoText,
                onMemoTextChange = { memoText = it }
            )
            Spacer(modifier = Modifier.weight(1f))
            AddDoneWriteButton(
                isEnabled = memoText.isNotEmpty(),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                onDoneClick = { navigateToAlbum() }
            )
        }

    }
}

@Composable
fun TitleAppBar() {
    Box(
        modifier = Modifier.fillMaxWidth().height(40.dp).background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "ic_back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        )
        Text(
            text = stringResource(R.string.text_record_write),
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun RecordWriteContentCard(
    memoText: String,
    onMemoTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(16.dp).wrapContentHeight()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = Color(0x0D000000),
                    ambientColor = Color(0x0D000000)
                ).clip(RoundedCornerShape(16.dp)) // 전체 카드에 클립 적용

        ) {
            Box(
                modifier = Modifier.fillMaxSize().matchParentSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.8f),
                                Color.White.copy(alpha = 0.6f)
                            )
                        ),
                    )
                    .blur(radius = 3.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth().wrapContentHeight()

            ) {
                SelectAlbumWidget(
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                RecordWriteField(
                    memoText = memoText,
                    onMemoTextChange = onMemoTextChange
                )

            }
        }
    }
}


@Composable
fun SelectAlbumWidget(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_dummy_dog),
                contentDescription = "null",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(2.dp))

        Box(
            modifier = Modifier.weight(1f)
                .aspectRatio(1f)

        ) {
            Image(
                painter = painterResource(R.drawable.ic_dummy_dog),
                contentDescription = "null",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun RecordWriteField(
    memoText: String,
    onMemoTextChange: (String) -> Unit
) {


    Column (
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp)
    ) {
        Text(
            text = "2025년 3월 18일",
            style = PetgrowTheme.typography.medium,
            color = black21,
            fontSize = 14.sp,
        )
        Spacer(modifier = Modifier.height(12.dp))
        RecordWriteMemoField(
            text = memoText,
            onTextChange = onMemoTextChange

        )
        Text(
            text = "${memoText.length}/30",
            style = PetgrowTheme.typography.regular,
            fontSize = 12.sp,
            color = gray60,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 4.dp, bottom = 12.dp)
        )

    }
}

@Composable
fun RecordWriteMemoField(text: String, onTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp)
            .background(
                grayF8)
            .border(BorderStroke(1.dp, grayf1), shape = RoundedCornerShape(8.dp))

    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                if (newText.length <= 30) {
                    onTextChange(newText)
                }

            },
            label = { Text("반려견과의 기록을 작성해보세요.") },
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
        Spacer(modifier = Modifier.height(36.dp))

    }
}

@Composable
fun AddDoneWriteButton(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onDoneClick: () -> Unit,
) {

    val buttonColor = if (isEnabled) purple6C else purple6C.copy(alpha = 0.4f)

    val cornerRadius = 12.dp
    Button(
        onClick = onDoneClick,
        modifier = Modifier.fillMaxWidth().then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Text(
            text = stringResource(R.string.text_complete),
            style = PetgrowTheme.typography.medium,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}
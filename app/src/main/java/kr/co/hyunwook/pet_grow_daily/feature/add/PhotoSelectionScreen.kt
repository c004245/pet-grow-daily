package kr.co.hyunwook.pet_grow_daily.feature.add

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleC4
import kr.co.hyunwook.pet_grow_daily.feature.album.EmptyTodayGrowRecordWidget
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.LoadGalleryImage


@Composable
fun PhotoSelectionScreen(onPhotoSelected: (String) -> Unit) {
    val context = LocalContext.current

    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                try {
                    // URI에 대한 영구 권한 요청
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    selectedImageUri = uri.toString()
                    errorMessage = null // 에러 메시지 초기화
                } catch (e: SecurityException) {
                    errorMessage = "권한 요청에 실패했습니다. 다시 시도해주세요."
                }
            } else {
                errorMessage = "이미지를 선택하지 못했습니다. 다시 시도해주세요."
            }
        }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(580.dp)
            .padding(start = 16.dp, bottom = 16.dp, end = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (selectedImageUri != null) {
                LoadGalleryImage(
                    uri = selectedImageUri.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White)
                )
            } else {
                EmptyTodayGrowRecordWidget(
                    isFullHeight = false,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_background_album),
                            contentDescription = "background_album"
                        )
                    }
                }
            }

            // 에러 메시지 표시
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    style = PetgrowTheme.typography.medium,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            ChoosePhotoSelectWidget(
                onPhotoSelected = {
                    galleryLauncher.launch("image/*") // 갤러리 호출
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = {
                if (selectedImageUri != null) onPhotoSelected(selectedImageUri.toString())
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = if (selectedImageUri != null) {
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
fun ChoosePhotoSelectWidget(onPhotoSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPhotoSelected()

            }
            .height(56.dp)
            .background(color = grayf1, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_picture_album),
                contentDescription = "camera"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.wrapContentWidth(),
                text = stringResource(id = R.string.text_gallery),
                fontSize = 12.sp,
                style = PetgrowTheme.typography.bold,
                color = black21
            )
        }
    }
}
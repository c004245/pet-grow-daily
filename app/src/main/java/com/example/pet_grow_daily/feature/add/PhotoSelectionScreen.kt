package com.example.pet_grow_daily.feature.add

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.theme.black21
import com.example.pet_grow_daily.core.designsystem.theme.gray86
import com.example.pet_grow_daily.core.designsystem.theme.grayf1
import com.example.pet_grow_daily.core.designsystem.theme.purple6C
import com.example.pet_grow_daily.core.designsystem.theme.purpleC4
import com.example.pet_grow_daily.feature.home.EmptyTodayGrowRecordWidget
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import java.io.File


@Composable
fun PhotoSelectionScreen(onPhotoSelected: (String) -> Unit) {
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri?.toString() // Save the selected gallery image URI
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
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White) // Optional: 동일한 배경색 설정
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Image",
                        modifier = Modifier.fillMaxSize(), // Box의 크기를 채움
                        contentScale = ContentScale.Crop // 이미지를 강제로 잘라서 크기 맞춤
                    )
                }
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
            Spacer(modifier = Modifier.height(16.dp))
            ChoosePhotoSelectWidget(
                onPhotoSelected = {
                    galleryLauncher.launch("image/*") // Open the gallery to select an image
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Button(
            onClick = {
                if (selectedImageUri != null) onPhotoSelected(selectedImageUri.toString()) },
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
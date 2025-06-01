package kr.co.hyunwook.pet_grow_daily.feature.main.profile

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleC4
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ProfileImagePage(
    selectedImageUri: String?,
    onImageSelected: (String) -> Unit,
    onCompleteClick: () -> Unit
) {

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            onImageSelected(it.toString())
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Text(
                modifier = Modifier.padding(top = 100.dp),
                text = stringResource(id = R.string.text_profile_image),
                style = PetgrowTheme.typography.bold,
                color = black21,
                fontSize = 24.sp
            )
            Spacer(Modifier.height(40.dp))
            Box(
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp,
                        color = grayDE,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .background(Color.White)
                    .clickable { launcher.launch("image/*") }, // 클릭 시 갤러리 열기

                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri == null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_profile_image_none),
                            contentDescription = "add"
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = stringResource(id = R.string.text_picture_add),
                            style = PetgrowTheme.typography.bold,
                            color = gray86,
                            fontSize = 14.sp,
                        )
                    }
                } else {
                    GlideImage(
                        model = selectedImageUri,
                        contentDescription = "저장할 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                }
            }
        }
        Button(
            onClick = {
                if (selectedImageUri != null) {
                    onCompleteClick()
                }
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
            colors = if (selectedImageUri != null) {
                ButtonDefaults.buttonColors(containerColor = purple6C)
            } else {
                ButtonDefaults.buttonColors(containerColor = purpleC4)
            }
        ) {
            Text(
                text = stringResource(id = R.string.text_start),
                style = PetgrowTheme.typography.bold,
                color = Color.White,
                fontSize = 14.sp
            )

        }
    }
}
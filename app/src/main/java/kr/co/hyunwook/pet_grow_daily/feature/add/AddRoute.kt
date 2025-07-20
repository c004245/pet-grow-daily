package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AddRoute(
    viewModel: AddViewModel = hiltViewModel(),
    navigateToRecordWrite: (List<String>) -> Unit,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Android 13+ (API 33) 이상에서는 READ_MEDIA_IMAGES, 이하에서는 READ_EXTERNAL_STORAGE 사용
    val permission =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }


    var selectedImages by remember { mutableStateOf(setOf<GalleryImage>()) }
    val isSelectionComplete = selectedImages.size == 2

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            // 권한이 승인되면 이미지 로드 시작
            viewModel.reloadImages()
        }
    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            viewModel.reloadImages()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    fun handleBackClick() {
        isVisible = false
        // 애니메이션 시간 후 실제 네비게이션 실행
        CoroutineScope(Dispatchers.Main).launch {
            delay(300) // 애니메이션 시간과 일치
            onBackClick()
        }
    }

    // Handle physical back press with the same animation
    BackHandler(enabled = isVisible) {
        handleBackClick()
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it }),
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddScreen(
                uiState = uiState,
                permissionGranted = permissionGranted,
                requestPermission = {
                    permissionLauncher.launch(permission)
                },
                selectedImages = selectedImages,
                isSelectionComplete = isSelectionComplete,
                onImageSelect = { image ->
                    selectedImages = when {
                        selectedImages.contains(image) -> selectedImages - image
                        selectedImages.size < 2 -> selectedImages + image
                        else -> selectedImages.drop(1).toSet() + image
                    }
                },
                onConfirmSelection = {

                    navigateToRecordWrite(selectedImages.map { it.uri.toString() })
                },
                onBackClick = ::handleBackClick
            )
        }
    }
}

@Composable
fun AddScreen(
    uiState: AddUiState,
    permissionGranted: Boolean,
    requestPermission: () -> Unit,
    selectedImages: Set<GalleryImage>,
    isSelectionComplete: Boolean,
    onImageSelect: (GalleryImage) -> Unit,
    onConfirmSelection: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (!permissionGranted) {
            PermissionRequiredContent(requestPermission)
        } else if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.images.isEmpty()) {
            Text("이미지가 없습니다.", modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TitleAppBar(onBackClick = onBackClick)
                PictureChooseMessageWidget(selectedImages)

                val groupedImages = uiState.images.groupBy { it.date }

                Box(
                    modifier = Modifier.weight(1f)
                ) {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        groupedImages.forEach { (date, images) ->
                            item {
                                DateHeader(date = date)
                            }
                            item {
                                ImagesGridForDate(
                                    images = images,
                                    selectedImages = selectedImages,
                                    onImageSelect = onImageSelect
                                )
                            }
                        }
                    }
                }

                AddNextPhotoButton(
                    isEnabled = isSelectionComplete,
                    onNextClick = onConfirmSelection,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp, start = 24.dp, end = 24.dp, top = 12.dp)
                )
            }
        }
    }
}

@Composable
fun TitleAppBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = "ic_back",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
                .clickable {
                    onBackClick()
                }
        )
        Text(
            text = stringResource(R.string.text_picture_add),
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun PictureChooseMessageWidget(selectedImages: Set<GalleryImage>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(purple6C.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "사진을 2개 선택해주세요. (${selectedImages.size}/2)",
            style = PetgrowTheme.typography.medium,
            color = purple6C,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

}
@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = PetgrowTheme.typography.regular,
        fontSize = 14.sp,
        color = black21,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
fun ImagesGridForDate(
    images: List<GalleryImage>,
    selectedImages: Set<GalleryImage>,
    onImageSelect: (GalleryImage) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(horizontal = 2.dp),
        modifier = Modifier.height(((images.size / 3 + if (images.size % 3 > 0) 1 else 0) * 120).dp)
    ) {
        items(images) { image ->
            val isSelected = selectedImages.contains(image)
            val selectionIndex = if (isSelected)
                selectedImages.indexOf(image)
            else
                -1

            GalleryImageItem(
                image = image,
                isSelected = isSelected,
                selectionIndex = selectionIndex,
                onSelect = { onImageSelect(image) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GalleryImageItem(
    image: GalleryImage,
    isSelected: Boolean,
    selectionIndex: Int = -1,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 4.dp,
                        color = purple6C
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onSelect() }
    ) {
        GlideImage(
            model = image.uri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            loading = placeholder(ColorPainter(Color.Gray.copy(alpha = 0.3f))),
            failure = placeholder(ColorPainter(Color.Red.copy(alpha = 0.3f)))
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(20.dp)
                .background(
                    color = if (isSelected) purple6C
                    else
                        Color.Gray,
                    shape = RoundedCornerShape(4.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected && selectionIndex >= 0) {
                Text(
                    text = "${selectionIndex + 1}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}


@Composable
fun PermissionRequiredContent(requestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("사진 접근 권한이 필요합니다")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = requestPermission) {
            Text("권한 요청")
        }
    }
}

@Composable
fun AddNextPhotoButton(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onNextClick: () -> Unit,
) {

    val buttonColor = if (isEnabled) purple6C else purple6C.copy(alpha = 0.4f)

    val cornerRadius = 12.dp
    Button(
        onClick = onNextClick,
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Text(
            text = stringResource(R.string.text_next),
            style = PetgrowTheme.typography.medium,
            color = Color.White,
            fontSize = 16.sp,
        )
    }
}
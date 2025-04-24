package kr.co.hyunwook.pet_grow_daily.feature.add

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.placeholder
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.LoadGalleryImage
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.room.util.copy

@Composable
fun AddRoute(
    viewModel: AddViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isVisible by remember { mutableStateOf(true) }
    var permissionGranted by remember { mutableStateOf(false) }


    var selectedImages by remember { mutableStateOf(setOf<GalleryImage>()) }
    val isSelectionComplete = selectedImages.size == 2

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = Modifier.fillMaxSize().zIndex(1f)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddScreen(
                uiState = uiState,
                onBackClick = {
                    isVisible = false
                },
                permissionGranted = permissionGranted,
                requestPermission = {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
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

                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    uiState: AddUiState,
    onBackClick: () -> Unit,
    permissionGranted: Boolean,
    requestPermission: () -> Unit,
    selectedImages: Set<GalleryImage>,
    isSelectionComplete: Boolean,
    onImageSelect: (GalleryImage) -> Unit,
    onConfirmSelection: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 갤러리") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (isSelectionComplete) {
                        Button(
                            onClick = onConfirmSelection,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text("완료")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
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
                    Text(
                        text = "사진을 2개 선택해주세요. (${selectedImages.size}/2)",
                        style = PetgrowTheme.typography.medium,
                        color = purple6C,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )

                    val groupedImages = uiState.images.groupBy { it.date }

                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        // 날짜별로 헤더와 이미지 그리드 추가
                        groupedImages.forEach { (date, images) ->
                            item {
                                DateHeader(date = date)
                            }
                            item {
                                ImagesGridForDate(images = images,
                                    selectedImages = selectedImages,
                                    onImageSelect = onImageSelect)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = PetgrowTheme.typography.medium,
        color = black21,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
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
        modifier = Modifier.height((images.size / 3 + if(images.size % 3 > 0) 1 else 0) * 120.dp)
    ) {
        items(images) { image ->
            val isSelected = selectedImages.contains(image)

            // 선택된 이미지의 인덱스 계산 (0 또는 1)
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
        modifier = Modifier.aspectRatio(1f)
            .padding(2.dp)
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 2.dp,
                        color = purple6C.copy(alpha = 0.8f)
                    )
                } else {
                    Modifier
                }
            ).clickable { onSelect() }
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
            modifier = Modifier.align(Alignment.TopEnd)
                .padding(4.dp).size(20.dp)
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
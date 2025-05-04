package kr.co.hyunwook.pet_grow_daily.feature.album

import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.GrowRecord
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purpleD9
import kr.co.hyunwook.pet_grow_daily.feature.main.NavigateEnum
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.formatDate
import kr.co.hyunwook.pet_grow_daily.util.formatTimestampToDateTime
import kr.co.hyunwook.pet_grow_daily.util.getCategoryItem
import kr.co.hyunwook.pet_grow_daily.util.getCategoryType
import kr.co.hyunwook.pet_grow_daily.util.getEmotionItem
import kr.co.hyunwook.pet_grow_daily.util.getMemoOrRandomQuote
import android.util.Log
import kotlin.math.absoluteValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AlbumRoute(
    paddingValues: PaddingValues,
    navigateToAdd: () -> Unit = {},
    navigateToAlbumImage: () -> Unit = {},
    viewModel: AlbumViewModel = hiltViewModel()
) {
    val albumRecord by viewModel.albumRecord.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumRecord()
    }
    AlbumScreen(
        albumRecord = albumRecord,
        navigateToAdd = navigateToAdd,
        navigateToAlbumImage = navigateToAlbumImage
    )
}

@Composable
fun AlbumScreen(
    albumRecord: List<AlbumRecord>,
    navigateToAdd: () -> Unit = {},
    navigateToAlbumImage: () -> Unit = {}
) {

    Box(
        modifier = Modifier.fillMaxSize().background(grayF8)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CommonTopBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.text_album_title),
                        style = PetgrowTheme.typography.bold
                    )
                },
                icon = {
                    IconButton(onClick = {
                        navigateToAlbumImage()
                    }) {
                        Image(
                            painter = painterResource(R.drawable.ic_album),
                            contentDescription = null,
                        )

                    }
                }
            )

            if (albumRecord.isNotEmpty()) {
                CustomAlbumListWidget(
                    modifier = Modifier.padding(top = 16.dp),
                    albumRecordItem = albumRecord,
                    navigateToAdd = navigateToAdd
                )
            } else {
                EmptyAlbumWidget(
                    navigateToAdd = navigateToAdd
                )
            }
        }
    }
}


@Composable
fun CustomAlbumListWidget(modifier: Modifier,
                          albumRecordItem: List<AlbumRecord>,
                          navigateToAdd: () -> Unit) {

    Box(modifier = modifier.fillMaxSize()) {
       LazyColumn(
           modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
           verticalArrangement = Arrangement.spacedBy(16.dp),
           contentPadding = PaddingValues(
               top = 16.dp,
               bottom = 80.dp
           ),
       ) {
           itemsIndexed(albumRecordItem) { index, item ->
               AlbumCard(
                   albumRecord = item,
                   modifier = Modifier.fillMaxWidth()
               )
           }
       }
       
       Box(
           modifier = Modifier
               .align(Alignment.BottomCenter)
               .fillMaxWidth()
               .padding(start = 16.dp, end  = 16.dp, bottom = 16.dp)
               .clickable { navigateToAdd() }
               .clip(RoundedCornerShape(14.dp))
               .background(purple6C)
       ) {
           Row(
               modifier = Modifier.fillMaxWidth(),
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center
           ) {
               Image(
                   painter = painterResource(R.drawable.ic_plus),
                   contentDescription = "add album"
               )
               Spacer(
                   modifier = Modifier.width(8.dp)
               )
               Text(
                   text = stringResource(R.string.text_picture_add),
                   color = Color.White,
                   fontSize = 14.sp,
                   style = PetgrowTheme.typography.medium,
                   modifier = Modifier.padding(top = 14.dp, bottom = 14.dp)
               )
           }
       }
   }
}

@Composable
fun AlbumCard(
    albumRecord: AlbumRecord,
    modifier: Modifier
) {
    Box(
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
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
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                AlbumImageWidget(
                    firstImageUri = albumRecord.firstImage,
                    secondImageUri = albumRecord.secondImage,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                AlbumText(
                    date = albumRecord.date,
                    content = albumRecord.content,
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                )

            }

        }
    }


}

//앨범 이미지 영역
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun AlbumImageWidget(
    firstImageUri: String,
    secondImageUri: String,
    modifier : Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f).aspectRatio(1f)
        ) {
            GlideImage(
                model = firstImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(2.dp))

        Box(
            modifier = Modifier.weight(1f).aspectRatio(1f)
        ) {
            GlideImage(
                model = secondImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

        }
    }
}


//앨범 텍스트 영역
@Composable
fun AlbumText(
    date: Long,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = formatDate(date),
            style = PetgrowTheme.typography.medium,
            fontSize = 14.sp,
            color = black21,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            color = gray5E,
            fontSize = 14.sp,
            style = PetgrowTheme.typography.regular,
            modifier = Modifier.fillMaxWidth()
        )

    }

}



@Composable
fun EmptyAlbumWidget(
    navigateToAdd: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.text_no_album),
                color = gray86,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                style = PetgrowTheme.typography.medium,
                modifier = Modifier.wrapContentWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier.wrapContentWidth()
                    .clickable {
                        navigateToAdd()
                    }
                    .clip(RoundedCornerShape(14.dp))
                    .background(purple6C)
            ) {
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_plus),
                        modifier = Modifier.padding(start = 24.dp),
                        contentDescription = "add album"
                    )
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text(
                        text = stringResource(R.string.text_picture_add),
                        color = Color.White,
                        fontSize = 14.sp,
                        style = PetgrowTheme.typography.medium,
                        modifier = Modifier.padding(top = 14.dp, bottom = 14.dp, end = 24.dp)
                    )

                }

            }

        }
    }
}

@Composable
fun TodayCardDescription(growRecord: GrowRecord) {
    Log.d(
        "HWO",
        "growRecord -> ${growRecord.categoryType} -- ${growRecord.emotionType} -- ${growRecord.memo} -- ${growRecord.timeStamp}"
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .wrapContentHeight(),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = getCategoryType(growRecord.categoryType), // 제목 텍스트
                color = Color.Black,
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(purpleD9),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = getCategoryItem(
                        categoryType = growRecord.categoryType,
                        NavigateEnum.ALBUM
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .width(24.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(purpleD9),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = getEmotionItem(emotionType = growRecord.emotionType),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Text(
            text = formatTimestampToDateTime(growRecord.timeStamp),
            color = gray86,
            fontSize = 12.sp,
            style = PetgrowTheme.typography.medium,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(grayF8)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                modifier = Modifier.padding(12.dp),
                text = getMemoOrRandomQuote(growRecord.memo),
                color = black21,
                fontSize = 12.sp,
                style = PetgrowTheme.typography.regular
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}


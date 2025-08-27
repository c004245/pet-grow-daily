package kr.co.hyunwook.pet_grow_daily.feature.order

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.delay
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.analytics.EventConstants
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.redF3
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import kr.co.hyunwook.pet_grow_daily.util.CommonAppBarOnlyButton
import kr.co.hyunwook.pet_grow_daily.util.MAX_ALBUM_COUNT
import kr.co.hyunwook.pet_grow_daily.util.MAX_ALBUM_INSTA_BOOK_COUNT
import kr.co.hyunwook.pet_grow_daily.util.TODAY_LIMIT_CREATE
import kr.co.hyunwook.pet_grow_daily.util.formatPrice

@Composable
fun OrderRoute(
    navigateToAlbumSelect: (OrderProduct) -> Unit,
    viewModel: OrderViewModel,
    orderProduct: OrderProduct,
    onBackClick: () -> Unit
) {

    val albumRecord by viewModel.albumRecord.collectAsState()
    val todayOrderCount by viewModel.todayZipFileCount.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getAlbumSelectRecord()
        viewModel.getTodayOrderCount()
        viewModel.setCurrentOrderProduct(orderProduct)
    }

    BackHandler {
        onBackClick()
    }

    OrderScreen(
        albumRecord = albumRecord,
        orderProduct = orderProduct,
        onClickRequestPayment = {
            viewModel.addEvent(EventConstants.CLICK_ORDER_START_EVENT)
            navigateToAlbumSelect(orderProduct)

        },
        onBackClick = onBackClick,
        todayOrderCount = todayOrderCount,
        onClickOrderNotification = {
            Toast.makeText(context, "내일 9시에 알림을 보내드릴게요!", Toast.LENGTH_SHORT)
                .show()
            viewModel.scheduleOrderAvailableNotification()
        }
    )
}

@Composable
fun OrderScreen(
    albumRecord: List<AlbumRecord>,
    orderProduct: OrderProduct,
    onClickRequestPayment: () -> Unit,
    onBackClick: () -> Unit,
    todayOrderCount: Int,
    onClickOrderNotification: () -> Unit
) {
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(grayF8)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            CommonAppBarOnlyButton {
                onBackClick()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(13.dp))
                ImageSliderWithIndicator(orderProduct.productTitle)
                Spacer(Modifier.height(24.dp))
                ProductInfoWidget(orderProduct = orderProduct)
                Spacer(Modifier.height(16.dp))
                OrderCountWidget(todayOrderCount = todayOrderCount)
                Spacer(
                    Modifier
                        .height(16.dp)
                        .background(grayF8)
                )
                ProductDescriptionWidget(orderProduct.productDescription)
                Spacer(Modifier.height(200.dp)) // 버튼 높이만큼 여유공간 확보
            }
        }
        if (todayOrderCount == TODAY_LIMIT_CREATE) {
            NotificationButtonWidget(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClickOrderNotification = onClickOrderNotification
            )
        } else {
            val maxAlbumCount =
                if (orderProduct.id == 0) MAX_ALBUM_INSTA_BOOK_COUNT else MAX_ALBUM_COUNT
            OrderButtonWidget(
                albumRequireCount = maxAlbumCount - albumRecord.size * 2,
                onClickRequestPayment = onClickRequestPayment,
                isButtonEnabled = maxAlbumCount <= albumRecord.size * 2,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSliderWithIndicator(title: String) {
    val lightImages = remember {
        listOf(
            R.drawable.ic_product_light_submain1,
            R.drawable.ic_product_light_submain2,
            R.drawable.ic_product_light_submain3,
            R.drawable.ic_product_light_submain4,
        )
    }

    val middleImages = remember {
        listOf(
            R.drawable.ic_product_middle_submain1,
            R.drawable.ic_product_middle_submain2,
            R.drawable.ic_product_middle_submain3,
            R.drawable.ic_product_middle_submain4,
        )
    }

    val advanceImages = remember {
        listOf(
            R.drawable.ic_product_advance_submain1,
            R.drawable.ic_product_advance_submain2,
            R.drawable.ic_product_advance_submain3,
            R.drawable.ic_product_advance_submain4,

        )
    }

    val images = remember(title) {
        when {
            title.contains("인스타북") -> lightImages
            title.contains("하드") -> middleImages
            title.contains("패브릭") -> advanceImages
            else -> lightImages
        }
    }

    val pagerState = androidx.compose.foundation.pager.rememberPagerState(
        initialPage = 0,
        pageCount = { images.size }
    )

    LaunchedEffect(key1 = pagerState) {
        while (true) {
            delay(5000)
            val nextPage = if (pagerState.currentPage + 1 < images.size) {
                pagerState.currentPage + 1
            } else {
                0
            }
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Slider Image $page",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { iteration ->
                val color = if (pagerState.currentPage == iteration)
                    Color.White
                else
                    gray86


                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color = color)
                        .size(8.dp)
                )
            }

        }
    }
}

@Composable
fun ProductInfoWidget(
    orderProduct: OrderProduct
) {

    val discountAmount = orderProduct.productCost * (100 - orderProduct.productDiscount) / 100
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = orderProduct.productTitle,
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 18.sp,
            lineHeight = 18.sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = formatPrice(orderProduct.productCost),
            style = PetgrowTheme.typography.medium,
            textDecoration = TextDecoration.LineThrough,
            color = gray86,
            fontSize = 13.sp,
            lineHeight = 13.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "${orderProduct.productDiscount}%",
                style = PetgrowTheme.typography.bold,
                color = redF3,
                fontSize = 22.sp,
                lineHeight = 22.sp,
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = formatPrice(discountAmount),
                color = black21,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                style = PetgrowTheme.typography.bold
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "배송비 3,000원 포함!",
                style = PetgrowTheme.typography.medium,
                color = gray86,
                fontSize = 14.sp,
                lineHeight = 14.sp
            )
        }

    }

}

@Composable
fun OrderCountWidget(todayOrderCount: Int) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .background(grayf1, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier.padding(start = 16.dp),
                text = "오늘 제작 가능 수량",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = black21
            )
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "${TODAY_LIMIT_CREATE - todayOrderCount}개",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = purple6C
            )
        }

    }
}

@Composable
fun OrderButtonWidget(
    albumRequireCount: Int,
    isButtonEnabled: Boolean,
    onClickRequestPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, grayF8),
                    startY = 0f,
                    endY = 100f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(purple6C, RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp)),
            ) {

                var message = if (isButtonEnabled) {
                    "앨범 주문이 가능해요 😀"
                }
                else {
                    "사진을 ${albumRequireCount}개 더 채우면 주문 가능해요."
                }

                Text(
                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp),
                    text = message,
                    style = PetgrowTheme.typography.bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_down_order),
                contentDescription = null
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isButtonEnabled) purple6C else purple6C.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        //결제 테스트를 위한 주석 필요
                        if (isButtonEnabled) {
                        onClickRequestPayment()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 14.dp),
                    text = "앨범 주문",
                    style = PetgrowTheme.typography.bold,
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ProductDescriptionWidget(description: String) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        style = PetgrowTheme.typography.regular,
        color = black21,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        text = description

    )
}

@Composable
fun PaymentWebView(
    orderProduct: OrderProduct,
    paymentData: Map<String, String>?,
    onPaymentResult: (Boolean, String?, String?) -> Unit,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current

    BackHandler { onBackPressed() }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                )
                webChromeClient = object : android.webkit.WebChromeClient() {}
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (url?.contains("payment.html") == true) {
                            val name = paymentData?.get("name") ?: orderProduct.productTitle
                            val amount = paymentData?.get("amount") ?: "100"
                            val merchantUid = paymentData?.get("merchant_uid")
                                ?: ("test_" + System.currentTimeMillis())
                            val jsCode = """
                                if (typeof updateProductInfo === 'function') {
                                    updateProductInfo(\"$name\", \"$amount\", \"$merchantUid\");
                                }
                            """.trimIndent()
                            view?.evaluateJavascript(jsCode, null)
                        }
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        if (url == null) return false
                        return when {
                            url.startsWith("intent://") || url.startsWith("kakaopay://") || url.startsWith(
                                "kakaotalk://"
                            ) -> {
                                try {
                                    val intent = android.content.Intent.parseUri(
                                        url,
                                        android.content.Intent.URI_INTENT_SCHEME
                                    )
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                                true
                            }

                            else -> false
                        }
                    }
                }
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                }
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onPaymentSuccess(transactionId: String, amount: String) {
                        onPaymentResult(true, null, transactionId)
                    }
                    @JavascriptInterface
                    fun onPaymentFailure(message: String) {
                        onPaymentResult(false, message, null)
                    }
                }, "Android")
                loadUrl("file:///android_asset/payment.html")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun NotificationButtonWidget(
    modifier: Modifier = Modifier,
    onClickOrderNotification: () -> Unit
) {
    var timeRemaining by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            val currentTime = java.util.Calendar.getInstance()
            val targetTime = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)

                // 현재 시간이 9시를 넘었다면 다음날 9시로 설정
                if (currentTime.timeInMillis >= this.timeInMillis) {
                    add(java.util.Calendar.DAY_OF_MONTH, 1)
                }
            }

            val diffInMillis = targetTime.timeInMillis - currentTime.timeInMillis

            if (diffInMillis > 0) {
                val hours = (diffInMillis / (1000 * 60 * 60)) % 24
                val minutes = (diffInMillis / (1000 * 60)) % 60
                val seconds = (diffInMillis / 1000) % 60

                timeRemaining = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            } else {
                timeRemaining = "00:00:00"
            }

            delay(1000)
        }
    }

    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, grayF8),
                    startY = 0f,
                    endY = 100f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(100.dp),
                        ambientColor = Color.Black.copy(alpha = 0.1f),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .background(Color.White, RoundedCornerShape(100.dp))
                    .clip(RoundedCornerShape(100.dp)),
            ) {
                Text(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 14.dp),
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = purple6C)) {
                            append(timeRemaining)
                        }
                        withStyle(style = SpanStyle(color = black21)) {
                            append("이후 주문 가능해요.")
                        }
                    },
                    style = PetgrowTheme.typography.bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        purple6C,
                        RoundedCornerShape(14.dp)
                    )
                    .clip(RoundedCornerShape(14.dp))
                    .clickable {
                        onClickOrderNotification()
                    },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_bell),
                        contentDescription = "bell"
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        modifier = Modifier.padding(vertical = 14.dp),
                        text = "주문 가능 알림 받기",
                        style = PetgrowTheme.typography.bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

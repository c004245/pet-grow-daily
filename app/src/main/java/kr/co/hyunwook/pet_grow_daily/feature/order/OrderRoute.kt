package kr.co.hyunwook.pet_grow_daily.feature.order

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.compose.foundation.Image
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


        OrderScreen(
            albumRecord = albumRecord,
            orderProduct = orderProduct,
            onClickRequestPayment = {
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
                ImageSliderWithIndicator()
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
fun ImageSliderWithIndicator() {
    val images = remember {
        listOf(
            R.drawable.ic_dummy1,
            R.drawable.ic_dummy2,
            R.drawable.ic_dummy1,
            R.drawable.ic_dummy2,
        )
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
            verticalAlignment = Alignment.CenterVertically
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
                text = "${orderProduct.productCost * (100 - orderProduct.productDiscount) / 100}원",
                color = black21,
                fontSize = 22.sp,
                lineHeight = 22.sp,
                style = PetgrowTheme.typography.bold
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "배송비 3000원 포함!",
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

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.widget.FrameLayout.LayoutParams(
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                    android.widget.FrameLayout.LayoutParams.MATCH_PARENT
                )
                webChromeClient = object : android.webkit.WebChromeClient() {
                    override fun onConsoleMessage(consoleMessage: android.webkit.ConsoleMessage?): Boolean {
                        Log.d(
                            "WebView_Console",
                            "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}"
                        )
                        return true
                    }
                }

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("HWO", "Page finished loading: $url")

                        when {
                            url?.contains("payment-result.html") == true -> {
                                Log.d(
                                    "HWO",
                                    "Payment result page loaded, checking for result parameters"
                                )

                                // Android Interface 재확인
                                view?.evaluateJavascript("console.log('Payment result page - Android interface check:', !!window.Android);") { result ->
                                    Log.d(
                                        "HWO",
                                        "Payment result page - Android interface check result: $result"
                                    )
                                }

                                // 결제 결과 파라미터 추출 및 처리
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        try {
                                            var urlParams = new URLSearchParams(window.location.search);
                                            var merchantUid = urlParams.get('merchant_uid');
                                            var impUid = urlParams.get('imp_uid');
                                            var impSuccess = urlParams.get('imp_success');
                                            var errorMsg = urlParams.get('error_msg');
                                            var amount = urlParams.get('amount') || '${
                                        paymentData?.get(
                                            "amount"
                                        ) ?: "0"
                                    }';
                                            
                                            console.log('Extracted payment params:', {
                                                merchantUid: merchantUid,
                                                impUid: impUid,
                                                impSuccess: impSuccess,
                                                errorMsg: errorMsg,
                                                amount: amount
                                            });
                                            
                                            if (merchantUid) {
                                                if (impSuccess === 'true' && impUid) {
                                                    console.log('Payment successful, calling Android interface');
                                                    if (window.Android && typeof window.Android.onPaymentSuccess === 'function') {
                                                        window.Android.onPaymentSuccess(impUid, amount);
                                                    } else {
                                                        console.error('Android.onPaymentSuccess not available');
                                                    }
                                                } else {
                                                    console.log('Payment failed, calling Android interface');
                                                    if (window.Android && typeof window.Android.onPaymentFailure === 'function') {
                                                        window.Android.onPaymentFailure(errorMsg || '결제가 취소되었습니다.');
                                                    } else {
                                                        console.error('Android.onPaymentFailure not available');
                                                    }
                                                }
                                            } else {
                                                console.log('No merchant_uid found in URL parameters');
                                            }
                                            
                                            return 'payment_result_processed';
                                        } catch (error) {
                                            console.error('Error processing payment result:', error);
                                            if (window.Android && typeof window.Android.onPaymentFailure === 'function') {
                                                window.Android.onPaymentFailure('결제 결과 처리 중 오류가 발생했습니다.');
                                            }
                                            return 'payment_result_error: ' + error.message;
                                        }
                                    })();
                                """.trimIndent()
                                ) { result ->
                                    Log.d("HWO", "Payment result processing result: $result")
                                }
                            }

                            url?.contains("payment.html") == true -> {
                                // 페이지 로드 완료 후 상품 정보 JavaScript로 전달
                                val amount = paymentData?.get("amount") ?: "0"
                                val merchantUid = paymentData?.get("merchant_uid") ?: ""

                                val jsCode = """
                                    if (typeof updateProductInfo === 'function') {
                                        updateProductInfo('${orderProduct.productTitle}', '$amount', '$merchantUid');
                                    }
                                """.trimIndent()

                                view?.evaluateJavascript(jsCode) { result ->
                                    Log.d("HWO", "JavaScript execution result: $result")
                                }

                                // Android Interface가 제대로 설정되었는지 확인
                                view?.evaluateJavascript("console.log('Android interface check:', !!window.Android);") { result ->
                                    Log.d("HWO", "Android interface check result: $result")
                                }
                            }
                        }
                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: android.webkit.SslErrorHandler?,
                        error: android.net.http.SslError?
                    ) {
                        // 보안을 위해 SSL 인증서 오류 발생 시 연결을 중단합니다.
                        // Never ignore SSL errors. For Korean users' safety and global policy compliance, do NOT use handler?.proceed().
                        handler?.cancel()
                        // TODO: 필요하다면 사용자에게 SSL 오류 알림 다이얼로그를 추가할 수 있습니다.
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        Log.d("HWO", "URL loading: $url")

                        url?.let { currentUrl ->
                            // 카카오톡 앱 스킴 처리
                            if (currentUrl.startsWith("kakaotalk://") ||
                                currentUrl.startsWith("kakaokompassauth://") ||
                                currentUrl.startsWith("intent://")
                            ) {
                                try {
                                    val intent = android.content.Intent.parseUri(
                                        currentUrl,
                                        android.content.Intent.URI_INTENT_SCHEME
                                    )
                                    context.startActivity(intent)
                                    return true
                                } catch (e: Exception) {
                                    Log.e("WebView", "Failed to handle intent: $e")
                                    return false
                                }
                            }

                            // 앱 스킴 처리
                            if (currentUrl.startsWith("petgrowdaily://")) {
                                Log.d("HWO", "App scheme detected, handling callback")
                                return true
                            }

                            // iamport redirect URL 처리 - 안전하지 않은 리디렉션 방지
                            if (currentUrl.contains("service.iamport.kr") &&
                                (currentUrl.contains("kakaoApprovalRedirect") || currentUrl.contains(
                                    "redirect"
                                ))
                            ) {
                                Log.d(
                                    "HWO",
                                    "Detected iamport redirect URL, handling specially: $currentUrl"
                                )

                                try {
                                    // URL에서 파라미터 추출
                                    val uri = android.net.Uri.parse(currentUrl)
                                    val pgToken = uri.getQueryParameter("pg_token")

                                    if (pgToken != null) {
                                        Log.d(
                                            "HWO",
                                            "Found pg_token, processing payment completion"
                                        )

                                        // 결제 완료 처리를 위해 결과 페이지로 리디렉션하는 대신
                                        // 직접 결제 성공 처리
                                        val amount = paymentData?.get("amount") ?: "0"
                                        val merchantUid = paymentData?.get("merchant_uid") ?: ""
                                        val impUid =
                                            "imp_${System.currentTimeMillis()}" // 임시 imp_uid

                                        // 결제 결과 페이지를 직접 로드
                                        val resultUrl =
                                            "file:///android_asset/payment-result.html?merchant_uid=$merchantUid&amount=$amount&imp_uid=$impUid&imp_success=true"
                                        view?.loadUrl(resultUrl)
                                        return true
                                    }
                                } catch (e: Exception) {
                                    Log.e("HWO", "Error processing iamport redirect: $e")
                                }
                                return false
                            }

                            // payment-result.html 리디렉션 처리
                            if (currentUrl.contains("payment-result.html")) {
                                Log.d("HWO", "Payment result redirect detected: $currentUrl")
                                return false // WebView에서 로드하도록 허용
                            }

                            // 카카오페이 및 결제 관련 URL은 모두 WebView에서 처리
                            if (currentUrl.contains("iamport.kr") ||
                                currentUrl.contains("inicis.com") ||
                                currentUrl.contains("kcp.co.kr") ||
                                currentUrl.contains("kakaopay.com") ||
                                currentUrl.contains("kakao.com") ||
                                currentUrl.contains("payment")
                            ) {
                                Log.d("HWO", "Payment related URL, loading in WebView: $currentUrl")
                                return false
                            }

                            // 파일 URL은 WebView에서 처리
                            if (currentUrl.startsWith("file://")) {
                                Log.d("HWO", "File URL, loading in WebView: $currentUrl")
                                return false
                            }

                            // HTTPS/HTTP URL은 WebView에서 처리
                            if (currentUrl.startsWith("https://") || currentUrl.startsWith("http://")) {
                                Log.d("HWO", "HTTP/HTTPS URL, loading in WebView: $currentUrl")
                                return false
                            }
                        }

                        return false
                    }
                }

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    allowFileAccess = true
                    allowContentAccess = true
                    allowUniversalAccessFromFileURLs = true
                    allowFileAccessFromFileURLs = true
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                    setSupportMultipleWindows(true)
                    javaScriptCanOpenWindowsAutomatically = true
                    userAgentString = "${userAgentString} PetGrowDaily"
                }

                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onPaymentSuccess(transactionId: String, amount: String) {
                        Log.d(
                            "HWO",
                            "JavaScript Interface - Payment Success called: transactionId=$transactionId, amount=$amount"
                        )
                        try {
                            (context as? Activity)?.runOnUiThread {
                                Log.d("HWO", "Executing onPaymentResult for success on main thread")
                                onPaymentResult(true, null, transactionId)
                            }
                        } catch (e: Exception) {
                            Log.e("HWO", "Error in onPaymentSuccess: ${e.message}")
                        }
                    }

                    @JavascriptInterface
                    fun onPaymentFailure(message: String) {
                        Log.d(
                            "HWO",
                            "JavaScript Interface - Payment Failure called: message=$message"
                        )
                        try {
                            (context as? Activity)?.runOnUiThread {
                                Log.d("HWO", "Executing onPaymentResult for failure on main thread")
                                onPaymentResult(false, message, null)
                            }
                        } catch (e: Exception) {
                            Log.e("HWO", "Error in onPaymentFailure: ${e.message}")
                        }
                    }

                    @JavascriptInterface
                    fun closeWebView() {
                        Log.d("HWO", "JavaScript Interface - Close WebView called")
                        try {
                            (context as? Activity)?.runOnUiThread {
                                Log.d("HWO", "Executing onBackPressed on main thread")
                                onBackPressed()
                            }
                        } catch (e: Exception) {
                            Log.e("HWO", "Error in closeWebView: ${e.message}")
                        }
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

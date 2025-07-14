package kr.co.hyunwook.pet_grow_daily.feature.order

import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.redF3
import kr.co.hyunwook.pet_grow_daily.core.model.PaymentResult
import kr.co.hyunwook.pet_grow_daily.feature.add.TitleAppBar
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.room.util.copy
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.JavascriptInterface
import android.os.Bundle

@Composable
fun OrderRoute(
    navigateToAlbumSelect: () -> Unit,
    viewModel: OrderViewModel,
    orderProductType: OrderProductType
) {

    val userAlbumCount by viewModel.userAlbumCount.collectAsState()
    val paymentData by viewModel.paymentData.collectAsState()
    val paymentResult by viewModel.paymentResult.collectAsState()

    val context = LocalContext.current
    val activity = context as Activity

    var showPaymentWebView by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUserAlbumCount()
    }

    LaunchedEffect(paymentData) {
        paymentData?.let { data ->
            Log.d("HWO", "결제 요청 데이터: $data")
            showPaymentWebView = true
        }
    }

    if (showPaymentWebView) {
        PaymentWebView(
            onPaymentResult = { success, message, transactionId ->
                if (success) {
                    viewModel.setPaymentResult(PaymentResult.Success(transactionId ?: "", "27000"))
                    Log.d("HWO", "message done -> ")

                    Toast.makeText(context, "결제가 완료되었습니다!", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.setPaymentResult(PaymentResult.Failure(message ?: "결제 실패"))
                    Log.d("HWO", "message error -> $message")
                    Toast.makeText(context, "결제에 실패했습니다: $message", Toast.LENGTH_SHORT).show()
                }
                showPaymentWebView = false
            },
            onBackPressed = {
                showPaymentWebView = false
            }
        )
    } else {
        OrderScreen(
            onClickRequestPayment = {
                navigateToAlbumSelect()
//                viewModel.requestKakaoPayPayment()
            }
        )
    }
}

@Composable
fun OrderScreen(
    onClickRequestPayment: () -> Unit

) {
    val scrollState = rememberScrollState()


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
                CommonTopBar(
                    title = {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(id = R.string.text_order_title),
                            style = PetgrowTheme.typography.bold,
                            color = black21
                        )
                    }
                )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(Modifier.height(13.dp))
                ImageSliderWithIndicator()
                Spacer(Modifier.height(24.dp))
                ProductDescriptionWidget()
                Spacer(Modifier.height(16.dp))
                OrderCountWidget()
            }
        }
            OrderButtonWidget(
                onClickRequestPayment = onClickRequestPayment,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            )
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
            delay (5000)
            val nextPage = if (pagerState.currentPage + 1< images.size) {
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
            Log.d("HWO", "images -> ${images.size}")
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


//추후에 Firebase remote config 로 가격 조정
@Composable
fun ProductDescriptionWidget() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)

    ) {
        Text(
            text = "코팅형 고급 앨범 (사진 최대 40장)",
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 18.sp
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "30,000원",
            style = PetgrowTheme.typography.medium,
            textDecoration = TextDecoration.LineThrough,
            color = gray86,
            fontSize = 13.sp,
        )
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "10%",
                style = PetgrowTheme.typography.bold,
                color = redF3,
                fontSize = 22.sp
            )
            Spacer(Modifier.width(2.dp))
            Text(
                text = "27,000원",
                color = black21,
                fontSize = 22.sp,
                style = PetgrowTheme.typography.bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = "배송비 3,000원",
            style = PetgrowTheme.typography.medium,
            color = gray86,
            fontSize = 14.sp
        )
    }

}

@Composable
fun OrderCountWidget() {
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
                text = "오늘 주문 가능 수량",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = black21
            )
            Text(
                modifier = Modifier.padding(end = 16.dp),
                text = "10개",
                fontSize = 16.sp,
                style = PetgrowTheme.typography.bold,
                color = purple6C
            )
        }

    }
}

@Composable
fun OrderButtonWidget(
    onClickRequestPayment: () -> Unit,
    modifier: Modifier = Modifier) {
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .background(purple6C, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Text(
                modifier = Modifier.padding(vertical = 6.dp, horizontal = 14.dp),
                text = "사진을 6개 더 채우면 주문 가능해요.",
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
                .background(purple6C, RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                    onClickRequestPayment()
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

@Composable
fun PaymentWebView(
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
                        Log.d("WebView_Console", "${consoleMessage?.message()} -- From line ${consoleMessage?.lineNumber()} of ${consoleMessage?.sourceId()}")
                        return true
                    }
                }


                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        Log.d("HWO", "Page finished loading: $url")
                    }

                    override fun onReceivedSslError(
                        view: WebView?,
                        handler: android.webkit.SslErrorHandler?,
                        error: android.net.http.SslError?
                    ) {
                        // 개발/테스트용 - 실제 배포 시에는 적절한 SSL 검증 로직 필요
                        handler?.proceed()
                    }

                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        Log.d("HWO", "URL loading: $url")

                        url?.let {
                            // 카카오톡 앱 스킴 처리
                            if (it.startsWith("kakaotalk://") ||
                                it.startsWith("kakaokompassauth://") ||
                                it.startsWith("intent://")
                            ) {
                                try {
                                    val intent = android.content.Intent.parseUri(
                                        it,
                                        android.content.Intent.URI_INTENT_SCHEME
                                    )
                                    context.startActivity(intent)
                                    return true
                                } catch (e: Exception) {
                                    Log.e("WebView", "Failed to handle intent: $e")
                                }
                            }

                            // iamport 승인 URL 처리 - 이 부분을 추가하세요!
                            if (it.contains("service.iamport.kr") && it.contains("kakaoApprovalRedirect")) {
                                Log.d("HWO", "iamport 승인 완료, 결과 페이지로 이동")
                                // 결제 성공으로 간주하고 결과 페이지로 이동
                                val resultUrl = "file:///android_asset/payment-result.html?imp_success=true&imp_uid=결제완료"
                                view?.loadUrl(resultUrl)
                                return true
                            }

                            // 일반 HTTPS URL 처리
                            if (it.startsWith("https://") || it.startsWith("http://")) {
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
                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW // 추가
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                    setSupportMultipleWindows(true)
                    javaScriptCanOpenWindowsAutomatically = true
                    userAgentString = "${userAgentString} PetGrowDaily"
                }

                // JavaScript Interface 추가
                addJavascriptInterface(object {
                    @JavascriptInterface
                    fun onPaymentSuccess(transactionId: String, amount: String) {
                        Log.d("HWO", "Payment Success: $transactionId, $amount")
                        onPaymentResult(true, null, transactionId)
                    }

                    @JavascriptInterface
                    fun onPaymentFailure(message: String) {
                        Log.d("HWO", "Payment Failure: $message")
                        onPaymentResult(false, message, null)
                    }

                    @JavascriptInterface
                    fun closeWebView() {
                        Log.d("HWO", "Close WebView requested")
                        // 메인 스레드에서 실행
                        (context as? Activity)?.runOnUiThread {
                            onBackPressed()
                        }
                    }
                }, "Android")

                loadUrl("file:///android_asset/payment.html")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

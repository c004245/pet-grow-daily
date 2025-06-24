package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery

import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayAD
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.feature.add.TitleAppBar
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import org.json.JSONObject

@Composable
fun DeliveryRoute(

) {

    LaunchedEffect(Unit) {


    }
    DeliveryScreen(

    )


}

@Composable
fun TitleDeliveryAppBar() {
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
                .clickable {

                }
        )
        Text(
            text = stringResource(R.string.text_delivery_add_title),
            style = PetgrowTheme.typography.bold,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DeliveryScreen() {
    var address by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showWebView by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TitleDeliveryAppBar()
        AddressNumberWidget()
        Spacer(Modifier.height(8.dp))
        AddressDetailWidget()

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = grayDE
        )

        Spacer(Modifier.height(16.dp))
        UserInfoDeliveryWidget()


    }

    // WebView 다이얼로그
    if (showWebView) {
        DaumPostcodeWebView(
            onAddressSelected = { selectedZipCode, selectedAddress ->
                zipCode = selectedZipCode
                address = selectedAddress
                showWebView = false
            },
            onDismiss = { showWebView = false }
        )
    }
}

@Composable
fun AddressNumberWidget() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.weight(1f).height(58.dp)
                    .background(color = grayf1, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = stringResource(R.string.text_delivery_address_number),
                    color = grayAD,
                    style = PetgrowTheme.typography.regular,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {},
                modifier = Modifier.height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, grayDE)
            ) {
                Text(
                    text = stringResource(R.string.text_address_search),
                    color = black21,
                    style = PetgrowTheme.typography.bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AddressDetailWidget() {
    var address by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            placeholder = { Text("주소") },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = grayDE,
                unfocusedBorderColor = grayDE
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = detailAddress,
            onValueChange = { detailAddress = it },
            placeholder = { Text("상세 주소") },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = grayDE,
                unfocusedBorderColor = grayDE
            )
        )

    }
}


@Composable
fun UserInfoDeliveryWidget() {
    var userName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    Column {
        OutlinedTextField(
            value = userName,
            onValueChange = { userName = it },
            placeholder = { Text("받는 분") },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = grayDE,
                unfocusedBorderColor = grayDE
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { input ->
                val digitsOnly = input.filter { it.isDigit() }
                phoneNumber = when {
                    digitsOnly.length <= 3 -> digitsOnly
                    digitsOnly.length <= 7 -> "${digitsOnly.substring(0, 3)}-${
                        digitsOnly.substring(
                            3
                        )
                    }"

                    digitsOnly.length <= 11 -> "${
                        digitsOnly.substring(
                            0,
                            3
                        )
                    }-${digitsOnly.substring(3, 7)}-${digitsOnly.substring(7)}"

                    else -> "${digitsOnly.substring(0, 3)}-${
                        digitsOnly.substring(
                            3,
                            7
                        )
                    }-${digitsOnly.substring(7, 11)}"
                }
            },
            placeholder = { Text("휴대폰 번호") },
            modifier = Modifier.fillMaxWidth().height(58.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = grayDE,
                unfocusedBorderColor = grayDE
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

    }

}
@Composable
fun DaumPostcodeWebView(
    onAddressSelected: (zipCode: String, address: String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true

                        // JavaScript Interface 추가
                        addJavascriptInterface(
                            PostcodeJavaScriptInterface { zipCode, address ->
                                onAddressSelected(zipCode, address)
                            },
                            "Android"
                        )

                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()

                        // 다음 우편번호 서비스 HTML 로드
                        loadDataWithBaseURL(
                            null,
                            getDaumPostcodeHtml(),
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // 닫기 버튼
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    )
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "닫기",
                    tint = Color.White
                )
            }
        }
    }
}

class PostcodeJavaScriptInterface(
    private val onAddressSelected: (zipCode: String, address: String) -> Unit
) {
    @JavascriptInterface
    fun processDATA(data: String) {
        try {
            val json = JSONObject(data)
            val zipCode = json.getString("zonecode")
            val address = json.getString("roadAddress").takeIf { it.isNotEmpty() }
                ?: json.getString("jibunAddress")

            onAddressSelected(zipCode, address)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

private fun getDaumPostcodeHtml(): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
        </head>
        <body>
            <div id="layer" style="display:block;position:absolute;top:0;left:0;width:100%;height:100%;z-index:1"></div>
            <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
            <script>
                new daum.Postcode({
                    oncomplete: function(data) {
                        Android.processDATA(JSON.stringify(data));
                    },
                    width: '100%',
                    height: '100%',
                    maxSuggestItems: 5
                }).embed('layer');
            </script>
        </body>
        </html>
    """.trimIndent()
}

package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebResourceRequest
import android.webkit.WebResourceError
import android.webkit.WebViewClient
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayAD
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import org.json.JSONObject

@Composable
fun DeliveryAddRoute(
    viewModel: DeliveryViewModel = hiltViewModel(),
    navigateToDeliveryList: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.saveDeliveryDoneEvent.collectLatest { isSuccess ->
            if (isSuccess) {
                navigateToDeliveryList()
            }
        }
    }
    DeliveryAddScreen(onSaveClick = { deliveryInfo ->
        viewModel.saveDeliveryInfo(deliveryInfo)
    })
}

@Composable
fun DeliveryAddScreen(onSaveClick: (DeliveryInfo) -> Unit) {
    var address by remember { mutableStateOf("") }
    var zipCode by remember { mutableStateOf("") }
    var detailAddress by remember { mutableStateOf("") }
    var recipientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showWebView by remember { mutableStateOf(false) }
    var isDefaultDelivery by remember { mutableStateOf(false) }

    val isFormValid = zipCode.isNotEmpty() && address.isNotEmpty() &&
            detailAddress.isNotEmpty() && recipientName.isNotEmpty() && phoneNumber.isNotEmpty()

    Log.d(
        "DeliveryForm",
        "zipCode: '$zipCode', address: '$address', detailAddress: '$detailAddress', recipientName: '$recipientName', phoneNumber: '$phoneNumber'"
    )
    Log.d("DeliveryForm", "isFormValid: $isFormValid")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TitleDeliveryAppBar(stringResource(R.string.text_address_search))
        AddressNumberWidget(
            zipCode = zipCode,
            onAddressSearchClick = {
                showWebView = true
            }
        )
        Spacer(Modifier.height(8.dp))
        AddressDetailWidget(
            address = address,
            detailAddress = detailAddress,
            onAddressChange = { address = it },
            onDetailAddressChange = { detailAddress = it }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 1.dp,
            color = grayDE
        )

        Spacer(modifier = Modifier.height(16.dp))
        UserInfoDeliveryWidget(
            recipientName = recipientName,
            phoneNumber = phoneNumber,
            onRecipientNameChange = { recipientName = it },
            onPhoneNumberChange = { phoneNumber = it }
        )
        Spacer(modifier = Modifier.height(24.dp))
        CheckDefaultDeliveryWidget(
            isChecked = isDefaultDelivery,
            onCheckedChange = { isDefaultDelivery = it }
        )
        Spacer(Modifier.weight(1f))
        SaveButton(
            isEnabled = isFormValid,
            onSaveClick = {
                val deliveryInfo = DeliveryInfo(
                    zipCode = zipCode,
                    address = address,
                    detailAddress = detailAddress,
                    name = recipientName,
                    phoneNumber = phoneNumber,
                    isDefault = isDefaultDelivery
                )
                onSaveClick(deliveryInfo)
            }
        )
    }

    if (showWebView) {
        PostcodeDialog(
            onDismiss = { showWebView = false },
            onAddressSelected = { selectedZipCode, selectedAddress ->
                zipCode = selectedZipCode
                address = selectedAddress
                showWebView = false
            }
        )
    }
}

@Composable
fun SaveButton(
    isEnabled: Boolean,
    onSaveClick: () -> Unit
) {
    Button(
        onClick = onSaveClick,
        modifier = Modifier
            .fillMaxWidth(),
        enabled = isEnabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) purple6C else purple6C.copy(0.4f)
        )
    ) {
        Text(
            text = stringResource(id = R.string.text_save),
            style = PetgrowTheme.typography.bold,
            color = Color.White,
            fontSize = 14.sp
        )
    }

}

@Composable
fun AddressNumberWidget(
    zipCode: String,
    onAddressSearchClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(58.dp)
                    .background(color = grayf1, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = if (zipCode.isEmpty()) stringResource(R.string.text_delivery_address_number) else zipCode,
                    color = grayAD,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.width(8.dp))
            Button(
                onClick = onAddressSearchClick,
                modifier = Modifier.height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, grayDE)
            ) {
                Text(
                    text = stringResource(R.string.text_address_search),
                    color = black21,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun AddressDetailWidget(
    address: String,
    detailAddress: String,
    onAddressChange: (String) -> Unit,
    onDetailAddressChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = address,
            onValueChange = onAddressChange,
            placeholder = { Text("주소") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
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
            onValueChange = onDetailAddressChange,
            placeholder = { Text("상세 주소") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
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
fun UserInfoDeliveryWidget(
    recipientName: String,
    phoneNumber: String,
    onRecipientNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit
) {
    Column {
        OutlinedTextField(
            value = recipientName,
            onValueChange = onRecipientNameChange,
            placeholder = { Text("받는 분") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
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
                val formattedPhone = when {
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
                onPhoneNumberChange(formattedPhone)
            },
            placeholder = { Text("휴대폰 번호") },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
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
fun PostcodeDialog(
    onDismiss: () -> Unit,
    onAddressSelected: (zipCode: String, address: String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            AndroidView(factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        allowContentAccess = true
                        allowFileAccess = true
                        allowUniversalAccessFromFileURLs = true
                        allowFileAccessFromFileURLs = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        cacheMode = WebSettings.LOAD_NO_CACHE
                    }

                    addJavascriptInterface(
                        PostcodeJavaScriptInterface(onAddressSelected),
                        "Android"
                    )

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(
                            view: WebView?,
                            url: String?,
                            favicon: android.graphics.Bitmap?
                        ) {
                            super.onPageStarted(view, url, favicon)
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            super.onPageFinished(view, url)
                            view.evaluateJavascript("typeof daum !== 'undefined'") { result ->
                                if (result == "true") {
                                    view.evaluateJavascript("typeof daum.Postcode !== 'undefined'") { postcodeResult ->
                                    }
                                }
                            }
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                        }
                    }

                    loadDataWithBaseURL(
                        "https://postcode.map.daum.net/",
                        """
                        <!DOCTYPE html>
                        <html>
                        <head>
                            <meta charset="UTF-8">
                            <meta name="viewport" content="width=device-width, initial-scale=1.0">
                            <title>우편번호 검색</title>
                        </head>
                        <body style="margin:0; padding:0;">
                            <div id="wrap" style="display:none;border:1px solid;width:500px;height:300px;margin:5px 0;position:relative">
                                <img src="//t1.daumcdn.net/postcode/resource/images/close.png" id="btnFoldWrap" style="cursor:pointer;position:absolute;right:0px;top:-1px;z-index:1" onclick="foldDaumPostcode()" alt="접기 버튼">
                            </div>
                            
                            <script src="//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
                            <script>
                                var element_wrap = document.getElementById('wrap');
                                
                                function foldDaumPostcode() {
                                    element_wrap.style.display = 'none';
                                }
                                
                                new daum.Postcode({
                                    oncomplete: function(data) {
                                        // JavaScript Interface를 통해 안드로이드로 데이터 전달
                                        Android.processDATA(JSON.stringify(data));
                                        // iframe을 넣은 element를 안보이게 한다.
                                        element_wrap.style.display = 'none';
                                    },
                                    onresize : function(size) {
                                        element_wrap.style.height = size.height+'px';
                                    },
                                    width : '100%',
                                    height : '100%'
                                }).embed(element_wrap);

                                // iframe을 넣은 element를 보이게 한다.
                                element_wrap.style.display = 'block';
                            </script>
                        </body>
                        </html>
                        """.trimIndent(),
                        "text/html",
                        "UTF-8",
                        null
                    )
                }
            })

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Close, "닫기")
            }
        }
    }
}

@Composable
fun CheckDefaultDeliveryWidget(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = purple6C,
                uncheckedColor = grayDE,
                checkmarkColor = Color.White
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            style = PetgrowTheme.typography.regular,
            text = "기본 배송지로 설정",
            color = black21,
            fontSize = 14.sp
        )
    }
}

class PostcodeJavaScriptInterface(
    private val onAddressSelected: (zipCode: String, address: String) -> Unit
) {
    @JavascriptInterface
    fun processDATA(data: String) {
        Log.d("HWO", "processData -> ${data}")
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

@Composable
fun TitleDeliveryAppBar(title: String) {
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
                .clickable { }
        )
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = black21,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

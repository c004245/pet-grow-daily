package kr.co.hyunwook.pet_grow_daily.feature.mypage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.feature.delivery.TitleDeliveryAppBar
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun BusinessInfoRoute(
    navigateToMyPage: () -> Unit,
    modifier: Modifier = Modifier,
) {

    BusinessInfoScreen(
        modifier = Modifier,
        navigateToMyPage = navigateToMyPage
    )
}

@Composable
fun BusinessInfoScreen(
    navigateToMyPage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(grayF8)
            .padding(24.dp)
    ) {
        TitleDeliveryAppBar(
            title = stringResource(R.string.text_business_info),
            navigateToBack = {
                navigateToMyPage()
            }
        )
        Text(
            text = "상호명: 매프\n대표자명: 조현욱\n사업장 주소: 서울시 강남구 헌릉로569길 21-30 417호\n사업자등록번호: 233-21-02544\n대표전화: 010-2732-7899\n대표 이메일: c004112@gmail.com",
            style = PetgrowTheme.typography.regular,
            color = gray5E,
            fontSize = 12.sp,
        )
    }
}
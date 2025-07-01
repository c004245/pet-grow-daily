import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryCheckRoute(

) {

    DeliveryCheckScreen()

}

@Composable
fun DeliveryCheckScreen(

) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TitleDeliveryAppBarOnlyButton( {

        })
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.text_delivery_check_title),
            style = PetgrowTheme.typography.medium,
            fontSize = 24.sp,
            color = black21
        )
        Spacer(Modifier.height(40.dp))

    }
}


@Composable
fun TitleDeliveryAppBarOnlyButton(
    navigateToBack: () -> Unit
) {
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
                .clickable { navigateToBack() }
        )
    }
}
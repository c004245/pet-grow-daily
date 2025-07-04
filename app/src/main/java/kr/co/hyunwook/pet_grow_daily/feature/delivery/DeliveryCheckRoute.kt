import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.feature.delivery.DeliveryInfoItem
import kr.co.hyunwook.pet_grow_daily.feature.order.OrderViewModel
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryCheckRoute(
    viewModel: OrderViewModel = hiltViewModel()
) {

    val deliveryInfos by viewModel.deliveryInfos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDeliveryList()
    }


    DeliveryCheckScreen(
        deliveryInfos = deliveryInfos
    )

}

@Composable
fun DeliveryCheckScreen(
    deliveryInfos: List<DeliveryInfo>
) {

    val selectedDeliveryId = remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(deliveryInfos) {
        if (selectedDeliveryId.value == null && deliveryInfos.isNotEmpty()) {
            selectedDeliveryId.value = deliveryInfos.find { it.isDefault }?.id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        TitleDeliveryAppBarOnlyButton({

        })
        Spacer(Modifier.height(40.dp))
        Text(
            text = stringResource(R.string.text_delivery_check_title),
            style = PetgrowTheme.typography.medium,
            fontSize = 24.sp,
            color = black21
        )
        Spacer(Modifier.height(40.dp))
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(deliveryInfos.size) { index ->
                val deliveryInfo = deliveryInfos[index]
                DeliveryInfoItem(
                    deliveryInfo = deliveryInfo,
                    isSelected = selectedDeliveryId.value == deliveryInfo.id,
                    onItemClick = {
                        selectedDeliveryId.value = deliveryInfo.id
                    },
                    onEditClick = {
//                        onEditClick(deliveryInfo.id)
                    },
                    onDeleteClick = {
//                        onDeleteClick(deliveryInfo.id)
                    }
                )
                if (index < deliveryInfos.size - 1) {
                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = grayDE
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                }
                .clip(RoundedCornerShape(14.dp))
                .background(purple6C)
        ) {
            Text(
                text = stringResource(R.string.text_confirm_done),
                color = Color.White,
                fontSize = 16.sp,
                style = PetgrowTheme.typography.medium,
                modifier = Modifier
                    .padding(top = 14.dp, bottom = 14.dp)
                    .align(Alignment.Center)
            )
        }

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
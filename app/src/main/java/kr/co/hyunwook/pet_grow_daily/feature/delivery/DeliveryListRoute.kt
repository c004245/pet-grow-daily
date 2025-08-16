package kr.co.hyunwook.pet_grow_daily.feature.delivery

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray5E
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayF8
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayf1
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryListRoute(
    navigateToMyPage: () -> Unit,
    navigateToDeliveryAdd: (Int?) -> Unit,
    viewModel: DeliveryViewModel = hiltViewModel()
) {

    val deliveryInfos by viewModel.deliveryInfos.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDeliveryList()
    }

    DeliveryListScreen(
        deliveryInfos = deliveryInfos,
        isLoading = isLoading,
        navigateToMyPage = navigateToMyPage,
        navigateToDeliveryAdd = navigateToDeliveryAdd,
        onEditClick = { deliveryId ->
            navigateToDeliveryAdd(deliveryId)
        },
        onDeleteClick = { deliveryId ->
            viewModel.deleteDeliveryInfo(deliveryId)
        }
    )

}

@Composable
fun DeliveryListScreen(
    deliveryInfos: List<DeliveryInfo>,
    isLoading: Boolean = false,
    navigateToMyPage: () -> Unit = {},
    navigateToDeliveryAdd: (Int?) -> Unit = {},
    onEditClick: (Int) -> Unit = {},
    onDeleteClick: (Int) -> Unit = {},
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(deliveryInfos) {
        if (selectedIndex == null && deliveryInfos.isNotEmpty()) {
            selectedIndex = deliveryInfos.find { it.isDefault }?.id
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(24.dp)
    ) {
        TitleDeliveryAppBar(stringResource(R.string.text_delivery_list), navigateToBack = {
            navigateToMyPage()
        })

        Log.d("HWO", "DeliveryList -> $isLoading -- ${deliveryInfos.isEmpty()}")
        if (isLoading) {
            // Loading state - show nothing or a loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (deliveryInfos.isEmpty()) {
            EmptyDeliveryState(
                navigateToDeliveryAdd = {
                    navigateToDeliveryAdd(null)
                }
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 24.dp)
            ) {
                items(deliveryInfos.size) { index ->
                    val deliveryInfo = deliveryInfos[index]
                    DeliveryInfoItem(
                        deliveryInfo = deliveryInfo,
                        isSelected = selectedIndex == deliveryInfo.id,
                        onItemClick = {
                            selectedIndex =
                                if (selectedIndex == deliveryInfo.id) null else deliveryInfo.id
                        },
                        onEditClick = {
                            onEditClick(deliveryInfo.id)
                        },
                        onDeleteClick = {
                            onDeleteClick(deliveryInfo.id)
                        },
                        isDeleteShow = true
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navigateToDeliveryAdd(null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(1.dp, grayDE)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_add_delivery),
                        contentDescription = "add_delivery"
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.text_new_delivery),
                        color = black21,
                        fontSize = 13.sp,
                        style = PetgrowTheme.typography.medium

                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDeliveryState(
    navigateToDeliveryAdd: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.text_empty_delivery_address),
            style = PetgrowTheme.typography.medium,
            fontSize = 14.sp,
            color = gray86
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            modifier = Modifier.height(48.dp),
            onClick = navigateToDeliveryAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, purple6C)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_delivery_plus),
                    contentDescription = "add"
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.text_new_delivery_add),
                    style = PetgrowTheme.typography.medium,
                    color = purple6C,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
fun DeliveryInfoItem(
    deliveryInfo: DeliveryInfo,
    isSelected: Boolean = false,
    onItemClick: () -> Unit = {},
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isDeleteShow: Boolean,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() }
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = deliveryInfo.name,
                    fontSize = 14.sp,
                    style = PetgrowTheme.typography.bold,
                    color = black21
                )

                Spacer(Modifier.width(8.dp))

                if (deliveryInfo.isDefault) {
                    Text(
                        text = "기본배송지",
                        color = purple6C,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .background(
                                color = purple6C.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "(${deliveryInfo.zipCode}) ${deliveryInfo.address} ${deliveryInfo.detailAddress}",
                    fontSize = 14.sp,
                    style = PetgrowTheme.typography.regular,
                    color = black21,
                )

                if (isSelected) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delivery_check),
                        contentDescription = "Selected icon"
                    )
                }
            }
            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${deliveryInfo.name}  ${deliveryInfo.phoneNumber}",
                    style = PetgrowTheme.typography.regular,
                    fontSize = 14.sp,
                    color = gray86
                )
            }

            Spacer(Modifier.height(20.dp))

            if (isDeleteShow) {
                Row {
                    Text(
                        text = "삭제",
                        color = gray5E,
                        fontSize = 14.sp,
                        style = PetgrowTheme.typography.medium,
                        modifier = Modifier
                            .clickable { onDeleteClick() }
                            .background(
                                color = grayf1,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

    }
}
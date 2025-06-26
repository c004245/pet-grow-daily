package kr.co.hyunwook.pet_grow_daily.feature.mypage.delivery

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.hilt.navigation.compose.hiltViewModel
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.DeliveryInfo
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayAD
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.grayDE
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun DeliveryListRoute(
    navigateToMyPage: () -> Unit,
    navigateToDeliveryAdd: () -> Unit,
    viewModel: DeliveryViewModel = hiltViewModel()
) {

    val deliveryInfos by viewModel.deliveryInfos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getDeliveryList()
    }

    DeliveryListScreen(
        deliveryInfos = deliveryInfos,
        navigateToMyPage = navigateToMyPage,
        navigateToDeliveryAdd = navigateToDeliveryAdd
    )

}

@Composable
fun DeliveryListScreen(
    deliveryInfos: List<DeliveryInfo>,
    navigateToMyPage: () -> Unit = {},
    navigateToDeliveryAdd: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(56.dp),
//            contentAlignment = Alignment.Center
//        ) {
//            Text(
//                text = stringResource(R.string.text_delivery_list),
//                fontSize = 18.sp,
//                fontWeight = FontWeight.Bold,
//                color = black21
//            )
//        }
        TitleDeliveryAppBar(stringResource(R.string.text_delivery_list))

        if (deliveryInfos.isEmpty()) {
            EmptyDeliveryState(
                navigateToDeliveryAdd = navigateToDeliveryAdd
            )
        } else {
            // 배송지가 있을 때
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(deliveryInfos) { deliveryInfo ->
                    DeliveryInfoItem(
                        deliveryInfo = deliveryInfo,
                        onEditClick = {
                            // 수정 기능 구현
                        },
                        onDeleteClick = {
                            // 삭제 기능 구현
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 새 배송지 추가 버튼
            Button(
                onClick = navigateToDeliveryAdd,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = purple6C
                )
            ) {
                Text(
                    text = "새 배송지 추가",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
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
            onClick = navigateToDeliveryAdd,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, purple6C)
        ) {
            Row(
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 24.dp),
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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, grayDE)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 기본 배송지 표시 (DeliveryInfo에 isDefault 필드가 있다고 가정)
                    if (deliveryInfo.isDefault == true) {
                        Text(
                            text = "기본 배송지",
                            color = purple6C,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    color = purple6C.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 받는 분
                    Text(
                        text = deliveryInfo.name ?: "", // or whatever the correct field name is
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = black21
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 주소
                    Text(
                        text = "${deliveryInfo.address ?: ""} ${deliveryInfo.detailAddress ?: ""}",
                        fontSize = 14.sp,
                        color = grayAD,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // 연락처
                    Text(
                        text = deliveryInfo.phoneNumber ?: "",
                        fontSize = 14.sp,
                        color = grayAD
                    )
                }

                // 수정/삭제 버튼
                Row {
                    TextButton(
                        onClick = onEditClick
                    ) {
                        Text(
                            text = "수정",
                            color = black21,
                            fontSize = 14.sp
                        )
                    }

                    TextButton(
                        onClick = onDeleteClick
                    ) {
                        Text(
                            text = "삭제",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

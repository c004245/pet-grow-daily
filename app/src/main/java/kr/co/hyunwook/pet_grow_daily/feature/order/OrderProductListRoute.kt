package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.database.entity.OrderProduct
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.gray86
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.purple6C
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.redF3
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumProgressWidget
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import java.text.NumberFormat
import java.util.Locale


//주문 상품 고를 수 있는 화면
@Composable
fun OrderProductListRoute(
    navigateToOrder: (OrderProductType) -> Unit,
    viewModel: OrderViewModel
) {

    val albumRecord by viewModel.albumRecord.collectAsState()
    val orderProducts by viewModel.orderProducts.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAlbumSelectRecord()
        viewModel.fetchOrderProducts()
    }

    OrderProductListScreen(
        albumRecord = albumRecord,
        orderProducts = orderProducts,
        navigateToOrder = navigateToOrder
    )
}

@Composable
fun OrderProductListScreen(
    albumRecord: List<AlbumRecord>,
    orderProducts: List<OrderProduct>,
    navigateToOrder: (OrderProductType) -> Unit,
) {
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
            Spacer(Modifier.height(16.dp))
            AlbumProgressWidget(
                albumRecord.size
            )
            Spacer(Modifier.height(20.dp))
            OrderProductListWidget(
                orderProducts = orderProducts,
                onProductClick = { productIndex ->
                    val orderProductType = when (productIndex) {
                        0 -> OrderProductType.INSTABOOK
                        1 -> OrderProductType.PHOTO_BOOK_LIGHT
                        2 -> OrderProductType.PHOTO_BOOK_PREMIUM
                        else -> OrderProductType.INSTABOOK
                    }
                    navigateToOrder(orderProductType)
                }
            )
        }
    }
}

@Composable
fun OrderProductListWidget(
    orderProducts: List<OrderProduct> = emptyList(),
    onProductClick: (Int) -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(orderProducts) { index, product ->
                OrderProductItem(
                    product = product,
                    index = index,
                    onItemClick = onProductClick
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OrderProductItem(
    product: OrderProduct,
    index: Int,
    onItemClick: (Int) -> Unit
) {

    val discountPrice = product.productCost * (100 - product.productDiscount) / 100
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
                onClick = { onItemClick(index) }
            )
            .padding(vertical = 16.dp, horizontal = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center

        ) {
            Image(
                painter = painterResource(id = getProductDrawableResource(product.productTitle)),
                contentDescription = product.productTitle,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = product.productTitle,
                style = PetgrowTheme.typography.medium,
                fontSize = 16.sp,
                lineHeight = 16.sp,
                color = black21
            )


            Text(
                text = formatPrice(product.productCost),
                style = PetgrowTheme.typography.medium,
                color = gray86,
                fontSize = 13.sp,
                lineHeight = 13.sp,
                textDecoration = TextDecoration.LineThrough  // 취소선
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${product.productDiscount}%",
                    style = PetgrowTheme.typography.medium,
                    color = redF3,
                    fontSize = 18.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = formatPrice(discountPrice),
                    style = PetgrowTheme.typography.medium,
                    color = black21,
                    fontSize = 18.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "배송비 포함",
                    style = PetgrowTheme.typography.medium,
                    color = gray86,
                    fontSize = 13.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

private fun getProductDrawableResource(productTitle: String): Int {
    return when (productTitle) {
        "인스타북" -> R.drawable.ic_order_dummy2
        "포토북 라이트" -> R.drawable.ic_order_dummy2
        "포토북 고급" -> R.drawable.ic_order_dummy2
        else -> R.drawable.ic_order_dummy2
    }
}

private fun formatPrice(price: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return "${formatter.format(price)}원"
}

enum class OrderProductType {
    INSTABOOK,
    PHOTO_BOOK_LIGHT,
    PHOTO_BOOK_PREMIUM
}
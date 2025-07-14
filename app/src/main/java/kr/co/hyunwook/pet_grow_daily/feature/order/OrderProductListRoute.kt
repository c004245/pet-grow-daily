package kr.co.hyunwook.pet_grow_daily.feature.order

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kr.co.hyunwook.pet_grow_daily.R
import kr.co.hyunwook.pet_grow_daily.core.database.entity.AlbumRecord
import kr.co.hyunwook.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.black21
import kr.co.hyunwook.pet_grow_daily.feature.album.AlbumProgressWidget
import kr.co.hyunwook.pet_grow_daily.ui.theme.PetgrowTheme
import java.text.NumberFormat
import java.util.Locale


//주문 상품 고를 수 있는 화면
@Composable
fun OrderProductListRoute(
    navigateToOrder: () -> Unit,
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
    navigateToOrder: () -> Unit,
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
                albumRecord.size,
                navigateToOrder = navigateToOrder
            )
            Spacer(Modifier.height(20.dp))
            OrderProductListWidget(orderProducts = orderProducts)
        }
    }
}

@Composable
fun OrderProductListWidget(
    orderProducts: List<OrderProduct> = emptyList()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(orderProducts) { index, product ->
                OrderProductItem(product = product)
            }
        }
    }
}

@Composable
fun OrderProductItem(
    product: OrderProduct
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 제품 이미지
                Image(
                    painter = painterResource(id = getProductDrawableResource(product.productTitle)),
                    contentDescription = product.productTitle,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                // 제품 정보
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.productTitle,
                        style = PetgrowTheme.typography.bold,
                        color = black21
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 할인 정보
                    if (product.productDiscount > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${product.productDiscount}%",
                                style = PetgrowTheme.typography.medium,
                                color = Color.Red
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = formatPrice(product.productCost),
                                style = PetgrowTheme.typography.medium,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    // 할인된 가격
                    val discountedPrice =
                        product.productCost * (100 - product.productDiscount) / 100
                    Text(
                        text = formatPrice(discountedPrice),
                        style = PetgrowTheme.typography.bold,
                        color = black21
                    )
                }
            }
        }
    }
}

private fun getProductDrawableResource(productTitle: String): Int {
    return when (productTitle) {
        "인스타북" -> R.drawable.ic_order_dummy
        "포토북 라이트" -> R.drawable.ic_order_dummy
        "포토북 고급" -> R.drawable.ic_order_dummy
        else -> R.drawable.ic_order_dummy
    }
}

private fun formatPrice(price: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
    return "${formatter.format(price)}원"
}
package com.example.pet_grow_daily.feature.total.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.pet_grow_daily.R
import com.example.pet_grow_daily.core.designsystem.component.topappbar.CommonTopBar
import com.example.pet_grow_daily.ui.theme.PetgrowTheme

@Composable
fun TotalRoute(
    paddingValues: PaddingValues
) {
    TotalScreen(
        paddingValues = paddingValues
    )
}

@Composable
fun TotalScreen(
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        CommonTopBar(
            title = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.text_total_title),
                    style = PetgrowTheme.typography.bold
                )
            }
        )
    }
}
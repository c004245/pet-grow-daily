package com.example.pet_grow_daily.core.designsystem.component.topappbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CommonTopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    ) {

    Box(
        modifier = modifier.
        fillMaxWidth().height(56.dp).padding(start = 16.dp, top = 16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            title()
        }
    }

}
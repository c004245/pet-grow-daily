package com.example.pet_grow_daily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pet_grow_daily.feature.main.MainNavigator
import com.example.pet_grow_daily.feature.main.MainScreen
import com.example.pet_grow_daily.feature.main.rememberMainNavigator
import com.example.pet_grow_daily.ui.theme.PetgrowdailyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PetgrowdailyTheme {
                val navigator: MainNavigator = rememberMainNavigator()
                // A surface container using the 'background' color from the theme
                MainScreen(navigator = navigator)
            }
        }
    }
}
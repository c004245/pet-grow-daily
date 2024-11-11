package com.example.pet_grow_daily

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.pet_grow_daily.feature.main.MainNavigator
import com.example.pet_grow_daily.feature.main.MainScreen
import com.example.pet_grow_daily.feature.main.rememberMainNavigator
import com.example.pet_grow_daily.ui.theme.PetgrowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen().setKeepOnScreenCondition { false }

        setContent {
            val navigator: MainNavigator = rememberMainNavigator()
            PetgrowTheme {
                CompositionLocalProvider {
                    MainScreen(navigator = navigator)
                }
                // A surface container using the 'background' color from the theme
            }
        }
    }
}
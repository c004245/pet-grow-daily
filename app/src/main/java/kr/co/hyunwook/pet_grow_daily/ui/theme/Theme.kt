package kr.co.hyunwook.pet_grow_daily.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.PetGrowTypography
import kr.co.hyunwook.pet_grow_daily.core.designsystem.theme.petGrowTypography

private val DarkColorScheme = lightColorScheme(
    primary = White.copy(alpha = 0.99f),
    background = White,
)

private val LightColorScheme = lightColorScheme(
    primary = White.copy(alpha = 0.99f),
    background = White,
)
private val LocalPetGrowTypography = staticCompositionLocalOf<PetGrowTypography> {
    error("No PetGrowTypography provided")
}
@Composable
fun ProvidePetGrowTypography(typography: PetGrowTypography, content: @Composable () -> Unit) {
    val provideTypography = remember { typography.copy() }
    provideTypography.update(typography)
    CompositionLocalProvider(
        LocalPetGrowTypography provides provideTypography,
        content = content

    )
}

object PetgrowTheme {
    val typography: PetGrowTypography
        @Composable get() = LocalPetGrowTypography.current
}
@Composable
fun PetgrowTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }
    val typography = petGrowTypography()

    // set status bar & navigation bar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            window.navigationBarColor = White.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = true
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightNavigationBars = true
        }
    }

    ProvidePetGrowTypography(typography = typography) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
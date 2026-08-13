package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ShopPrimaryDark,
    onPrimary = ShopOnPrimaryDark,
    primaryContainer = ShopPrimaryContainerDark,
    onPrimaryContainer = ShopOnPrimaryContainerDark,
    secondary = ShopSecondaryDark,
    onSecondary = ShopOnSecondaryDark,
    tertiary = ShopTertiaryDark,
    background = ShopBackgroundDark,
    surface = ShopSurfaceDark,
    onBackground = ShopOnSurfaceDark,
    onSurface = ShopOnSurfaceDark,
    outline = ShopOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = ShopPrimaryLight,
    onPrimary = ShopOnPrimaryLight,
    primaryContainer = ShopPrimaryContainerLight,
    onPrimaryContainer = ShopOnPrimaryContainerLight,
    secondary = ShopSecondaryLight,
    onSecondary = ShopOnSecondaryLight,
    tertiary = ShopTertiaryLight,
    background = ShopBackgroundLight,
    surface = ShopSurfaceLight,
    onBackground = ShopOnSurfaceLight,
    onSurface = ShopOnSurfaceLight,
    outline = ShopOutlineLight
)

@Composable
fun KashifMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set default false to preserve brand cyan theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

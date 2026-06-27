package com.agon.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextPrimary,
    primaryContainer = AccentDim,
    onPrimaryContainer = TextAccent,
    secondary = AccentLight,
    onSecondary = TextPrimary,
    secondaryContainer = BgCard,
    onSecondaryContainer = TextAccent,
    tertiary = AccentLight,
    onTertiary = TextPrimary,
    tertiaryContainer = BgPanel,
    onTertiaryContainer = TextAccent,
    background = BgBase,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    surfaceVariant = BgCard,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    surfaceTint = Accent,
    error = Error,
    onError = TextPrimary,
    errorContainer = ErrorDim,
    onErrorContainer = Error,
)

private val LightColorScheme = lightColorScheme(
    primary = Accent40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF5F0FF),
    onPrimaryContainer = Accent40,
    secondary = AccentGrey40,
    onSecondary = Color.White,
    background = Color(0xFFF8F7FF),
    onBackground = Color(0xFF1A1A2E),
    surface = Color.White,
    onSurface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFFF0EEFD),
    onSurfaceVariant = Color(0xFF5A5778),
    outline = Color(0xFFDEDCF5),
    error = Color(0xFFB91C1C),
)

@Composable
fun AgonAppTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

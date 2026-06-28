package com.example.energyconsumption.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EnergyColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = Color.White,
    primaryContainer = BrandGreenSoft,
    onPrimaryContainer = BrandGreenDark,
    secondary = BrandYellow,
    onSecondary = TextPrimary,
    secondaryContainer = BrandYellowSoft,
    onSecondaryContainer = AlertText,
    background = AppBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = BrandGreenPale,
    onSurfaceVariant = TextSecondary,
    outline = BorderSoft,
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun EnergyConsumptionTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EnergyColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.finalproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun clarityColorScheme(primary: Color) = lightColorScheme(
    primary = primary,
    onPrimary = Color.White,
    primaryContainer = ClarityPrimaryContainer,
    onPrimaryContainer = ClarityOnPrimaryContainer,
    secondary = ClaritySecondary,
    onSecondary = Color.White,
    secondaryContainer = ClaritySecondaryContainer,
    onSecondaryContainer = ClarityOnSecondaryContainer,
    tertiary = ClarityTertiary,
    onTertiary = Color.White,
    tertiaryContainer = ClarityTertiaryContainer,
    onTertiaryContainer = ClarityOnTertiaryContainer,
    error = ClarityError,
    onError = Color.White,
    errorContainer = ClarityErrorContainer,
    background = ClarityBackground,
    onBackground = ClarityOnSurface,
    surface = ClaritySurface,
    onSurface = ClarityOnSurface,
    surfaceVariant = ClaritySurfaceVariant,
    onSurfaceVariant = ClarityOnSurfaceVariant,
    outline = ClarityOutline,
    outlineVariant = ClarityOutlineVariant,
    surfaceContainerLowest = ClaritySurfaceContainerLowest,
    surfaceContainerLow = ClaritySurfaceContainerLow,
    surfaceContainer = ClaritySurfaceContainer,
    surfaceContainerHigh = ClaritySurfaceContainerHigh,
    surfaceContainerHighest = ClaritySurfaceContainerHighest,
    surfaceBright = ClaritySurfaceBright,
    surfaceDim = ClaritySurfaceDim,
)

@Composable
fun FinalProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    primaryColor: Color = ClarityPrimary,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = clarityColorScheme(primaryColor),
        typography = Typography,
        content = content
    )
}

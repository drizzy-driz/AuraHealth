package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF005234),
    onPrimaryContainer = Color(0xFF6FF7B8),
    secondary = TealAccent,
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFF73F8E7),
    tertiary = CyanAccent,
    onTertiary = Color(0xFF00363F),
    tertiaryContainer = Color(0xFF004E5B),
    onTertiaryContainer = Color(0xFF70F3FF),
    background = MedicalDeepNavy,
    onBackground = TextWhiteHigh,
    surface = MedicalNavySurface,
    onSurface = TextWhiteHigh,
    surfaceVariant = MedicalNavyCard,
    onSurfaceVariant = TextMediumSlate,
    outline = MedicalBorder,
    outlineVariant = Color(0xFF1E3250),
    error = AlertRose,
    onError = Color(0xFF680017),
    errorContainer = Color(0xFF920023),
    onErrorContainer = Color(0xFFFFDAD9)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to sleek medical dark mode per specification
    dynamicColor: Boolean = false, // Keep medical brand colors consistent
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val JarvisDarkColorScheme = darkColorScheme(
    primary = JarvisCyan,
    onPrimary = ObsidianDark,
    primaryContainer = ObsidianCard,
    onPrimaryContainer = JarvisCyan,
    secondary = JarvisCyanDim,
    onSecondary = ObsidianDark,
    tertiary = JarvisBlue,
    background = ObsidianDark,
    onBackground = TextPrimary,
    surface = ObsidianSurface,
    onSurface = TextPrimary,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = TextSecondary,
    outline = ObsidianBorder,
    error = AccentError,
    onError = ObsidianDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Jarvis Mini is a dedicated smart speaker UI, defaults to ambient dark
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = JarvisDarkColorScheme,
        typography = Typography,
        content = content
    )
}

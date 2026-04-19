package com.codex.izmireshot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors: ColorScheme = lightColorScheme(
    primary = Color(0xFF0F6B4F),
    secondary = Color(0xFF3E6B5B),
    tertiary = Color(0xFFF0A202),
    background = Color(0xFFF6F4EA),
    surface = Color(0xFFFFFCF3),
)

private val DarkColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF7DD7B8),
    secondary = Color(0xFFA8CDBD),
    tertiary = Color(0xFFFFC857),
    background = Color(0xFF101815),
    surface = Color(0xFF17211D),
)

@Composable
fun IzmirEshotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = MaterialTheme.typography,
        content = content,
    )
}

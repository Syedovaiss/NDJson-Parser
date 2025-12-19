package com.ovais.ndjsonparser.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    secondary = Secondary,
    onSecondary = White,
    tertiary = Tertiary,
    onTertiary = White,
    background = Background,
    onBackground = PrimaryText,
    surface = CardBackground,
    onSurface = PrimaryText,
    error = Error,
    onError = White
)

@Composable
fun NDJsonParserTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
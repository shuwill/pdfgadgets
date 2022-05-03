package org.spreadme.pdfgadgets.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import org.spreadme.pdfgadgets.utils.choose

val lightTheme = lightColors(
    primaryVariant = Color(0xFF4F52B2),
    primary = Color(0xFF5B5FC7),
    secondary = Color(0xFF005fB7),
    background = Color(0xFFE7EAED),
    surface = Color(0xFFF8F8F8),

    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF171717),
    onSurface = Color(0xFF262626),
)
val darkTheme = darkColors(
    primaryVariant = Color(0xFF2F2F4A),
    primary = Color(0xFF4F52B2),
    secondary = Color(0xFF604DFF),
    background = Color(0xFF1F1F1F),
    surface = Color(0xFF2D2C2C),

    onPrimary = Color(0xFFE5E5E5),
    onSecondary = Color(0xFF000000),
    onBackground = Color(0xFFF5F5F5),
    onSurface = Color(0xFFFAFAFA),
)

val lightExtraTheme = lightExtraColors()
val darkExtraTheme = darkExtraColors()

@Composable
fun PDFGadgetsTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colors = isDark.choose(darkTheme, lightTheme),
        typography = Typography,
        shapes = Shapes,
    ) {
        val extraTheme = isDark.choose(darkExtraTheme, lightExtraTheme)
        CompositionLocalProvider(
            LocalExtraColors provides extraTheme,
        ) {
            content()
        }
    }
}
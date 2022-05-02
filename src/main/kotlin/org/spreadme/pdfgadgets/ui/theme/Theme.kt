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
    primaryVariant = Color(0xFFE7EAED),
    secondary = Color(0xFF005FB7),
    background = Color(0xFFF4F4F5),
    surface = Color(0xFFFAFAFA),

    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFFFFFFFF),
    onBackground = Color(0xFF171717),
    onSurface = Color(0xFF262626),
)
val darkTheme = darkColors(
    primaryVariant = Color(0xFF18181B),
    secondary = Color(0xFF60CDFF),
    background = Color(0xFF27272A),
    surface = Color(0xFF2C2C2C),

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
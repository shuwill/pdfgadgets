package org.spreadme.pdfgadgets.ui.theme

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

class ExtraColors(
    success: Color,
    onSuccess: Color,
    successBackground: Color,

    error: Color,
    onError: Color,
    errorBackground: Color,

    warning: Color,
    onWarning: Color,
    warningBackground: Color,

    iconDisable: Color,
    contentDisable: Color,

    isLight: Boolean
) {
    var success by mutableStateOf(success, structuralEqualityPolicy())
        internal set
    var onSuccess by mutableStateOf(onSuccess, structuralEqualityPolicy())
        internal set
    var successBackground by mutableStateOf(successBackground, structuralEqualityPolicy())
        internal set

    var error by mutableStateOf(error, structuralEqualityPolicy())
        internal set
    var onError by mutableStateOf(onError, structuralEqualityPolicy())
        internal set
    var errorBackground by mutableStateOf(errorBackground, structuralEqualityPolicy())
        internal set

    var warning by mutableStateOf(warning, structuralEqualityPolicy())
        internal set
    var onWarning by mutableStateOf(onWarning, structuralEqualityPolicy())
        internal set
    var warningBackground by mutableStateOf(warningBackground, structuralEqualityPolicy())
        internal set

    var iconDisable by mutableStateOf(iconDisable, structuralEqualityPolicy())
        internal set
    var contentDisable by mutableStateOf(contentDisable, structuralEqualityPolicy())
        internal set

    var isLight by mutableStateOf(isLight, structuralEqualityPolicy())
        internal set
}

fun lightExtraColors(
    success: Color = Color(0xFF15803D),
    onSuccess: Color = Color(0xFF171717),
    successBackground: Color = Color(0xFFBBF7D0),

    error: Color = Color(0xFFB91C1C),
    onError: Color = Color(0xFF171717),
    errorBackground: Color = Color(0xFFFECACA),

    warning: Color = Color(0xFFC2410C),
    onWarning: Color = Color(0xFF171717),
    warningBackground: Color = Color(0xFFFED7AA),

    iconDisable: Color = Color(0xFFA3A3A3),
    contentDisable: Color = Color(0xFFA3A3A3),
): ExtraColors = ExtraColors(
    success,
    onSuccess,
    successBackground,

    error,
    onError,
    errorBackground,

    warning,
    onWarning,
    warningBackground,

    iconDisable,
    contentDisable,
    true
)


fun darkExtraColors(
    success: Color = Color(0xFF16A34A),
    onSuccess: Color = Color(0xFF171717),
    successBackground: Color = Color(0xFFBBF7D0),

    error: Color = Color(0xFFDC2626),
    onError: Color = Color(0xFF171717),
    errorBackground: Color = Color(0xFFFECACA),

    warning: Color = Color(0xFFEA580C),
    onWarning: Color = Color(0xFF171717),
    warningBackground: Color = Color(0xFFFED7AA),

    iconDisable: Color = Color(0xFF404040),
    contentDisable: Color = Color(0xFF404040),
): ExtraColors = ExtraColors(
    success,
    onSuccess,
    successBackground,

    error,
    onError,
    errorBackground,

    warning,
    onWarning,
    warningBackground,

    iconDisable,
    contentDisable,
    true
)


val LocalExtraColors = compositionLocalOf { lightExtraColors() }
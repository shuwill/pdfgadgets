package org.spreadme.pdfgadgets.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import java.awt.Cursor


@Composable
fun Modifier.clickable(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier{

    return this.pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR))).clickable(
        enabled = enabled,
        onClick = onClick,
        indication = null,
        interactionSource = remember { MutableInteractionSource() }
    )
}
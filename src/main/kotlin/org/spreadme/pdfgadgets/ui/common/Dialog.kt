package org.spreadme.pdfgadgets.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogWindow


@Composable
fun Dialog(
    onClose: () -> Unit,
    title: String,
    resizable: Boolean = true,
    content: @Composable (ColumnScope.() -> Unit)
) {
    DialogWindow(
        onCloseRequest = onClose,
        visible = true,
        title = title,
        resizable = resizable
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
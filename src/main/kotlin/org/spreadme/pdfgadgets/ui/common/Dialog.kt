package org.spreadme.pdfgadgets.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogState
import androidx.compose.ui.window.rememberDialogState
import org.spreadme.pdfgadgets.ui.PlatformUI


@Composable
fun Dialog(
    onClose: () -> Unit,
    title: String,
    resizable: Boolean = true,
    state: DialogState = rememberDialogState(),
    content: @Composable ColumnScope.() -> Unit
) {
    androidx.compose.ui.window.Dialog(
        onCloseRequest = onClose,
        title = title,
        state = state,
        resizable = resizable,
        visible = true
    ) {
        val platformUI = PlatformUI(window.rootPane)
        if(platformUI.isSupportCustomWindowDecoration()) {
            platformUI.customWindowDecoration()
        }
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)
        ) {
            if (platformUI.isCustomWindowDecoration()) {
                CustomWindowDecoration(
                    modifier = Modifier.fillMaxWidth().height(29.dp)
                        .background(MaterialTheme.colors.primaryVariant)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        title,
                        color = MaterialTheme.colors.onPrimary,
                        style = MaterialTheme.typography.caption,
                        textAlign = TextAlign.Center,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}
package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.model.OpenProperties
import org.spreadme.pdfgadgets.ui.common.*
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.ui.toolbars.ActionBar
import org.spreadme.pdfgadgets.ui.toolbars.Toolbars
import org.spreadme.pdfgadgets.ui.toolbars.ToolbarsViewModel

@Composable
fun MainApplicationFrame(
    toolbarsViewModel: ToolbarsViewModel,
    applicationViewModel: ApplicationViewModel,
    loadProgressViewModel: LoadProgressViewModel,
    content: @Composable RowScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.fillMaxSize()) {
            val progressState = remember { loadProgressViewModel }
            when (progressState.status) {
                LoadProgressStatus.LOADING -> LoadingModal()
                LoadProgressStatus.FAILURE -> {
                    FailureToast(progressState.message) {
                        progressState.status = LoadProgressStatus.FINISHED
                    }
                }
                LoadProgressStatus.NEED_PASSWORD -> {
                    EnterPasswordDialog(progressState.message) { password ->
                        if (password.isNotBlank()) {
                            progressState.loadPath?.let {
                                val openProperties = OpenProperties()
                                openProperties.password = password.toByteArray()
                                applicationViewModel.openFile(it, progressState, openProperties)
                            }
                        }
                        progressState.status = LoadProgressStatus.FINISHED
                    }
                }
                else -> {}
            }
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize().clickable { focusManager.clearFocus() }) {
                    ActionBar(applicationViewModel, progressState)
                    Column(Modifier.fillMaxSize()) {
                        Toolbars(
                            modifier = Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
                            toolbarsViewModel = toolbarsViewModel
                        )
                        Divider(color = LocalExtraColors.current.border)
                        Row(Modifier.fillMaxSize()) {
                            content()
                        }
                    }

                }
            }
        }
    }
}

@Composable
fun LoadingModal() {
    Box(
        Modifier.fillMaxSize()
            .background(MaterialTheme.colors.background.copy(alpha = 0.95f))
            .zIndex(999f),
        contentAlignment = Alignment.Center
    ) {
        LoadProgressIndicator()
    }
}

@Composable
fun FailureToast(message: String, onFinished: () -> Unit) {
    Box(
        Modifier.fillMaxSize().zIndex(999f).padding(top = 56.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Toast(
            message,
            ToastType.WARNING,
            onFinished = onFinished
        )
    }
}

@Composable
fun EnterPasswordDialog(
    message: String,
    onConfirm: (String) -> Unit
) {
    var enabled by remember { mutableStateOf(true) }
    if (enabled) {
        Dialog(
            onClose = {
                enabled = false
                onConfirm("")
            },
            title = "口令",
            resizable = false,
            state = rememberDialogState(width = 360.dp, height = 128.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().height(32.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "",
                    tint = LocalExtraColors.current.onWarning,
                    modifier = Modifier.padding(end = 8.dp).size(16.dp)
                )
                Text(
                    message,
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onBackground
                )
            }
            Row(
                Modifier.padding(horizontal = 8.dp).fillMaxWidth().height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var text by remember { mutableStateOf("") }
                TextInputField(
                    text,
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 8.dp)
                        .onPreviewKeyEvent {
                            if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                                onConfirm(text)
                                true
                            } else {
                                false
                            }
                        },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onSurface),
                    visualTransformation = PasswordVisualTransformation(),
                    onValueChange = { text = it },
                    trailingIcon = {
                        AnimatedVisibility(text.isNotBlank()) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "",
                                tint = MaterialTheme.colors.primary,
                                modifier = Modifier.padding(horizontal = 8.dp).size(16.dp).clickable {
                                    onConfirm(text)
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}
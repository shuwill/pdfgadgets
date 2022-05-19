package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.ui.common.LoadProgressIndicator
import org.spreadme.pdfgadgets.ui.common.Toast
import org.spreadme.pdfgadgets.ui.common.ToastType
import org.spreadme.pdfgadgets.ui.common.clickable
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
                        progressState.status = LoadProgressStatus.NONE
                    }
                }
                LoadProgressStatus.SUCCESSFUL -> progressState.onSuccess()
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
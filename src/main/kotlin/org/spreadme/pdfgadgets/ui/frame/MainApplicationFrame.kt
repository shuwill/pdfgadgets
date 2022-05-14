package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.ui.common.LoadProgressIndicator
import org.spreadme.pdfgadgets.ui.common.Toast
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
        Toolbars(
            modifier = Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
            toolbarsViewModel = toolbarsViewModel
        )
        Divider(color = LocalExtraColors.current.border)
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
                    Box(Modifier.fillMaxHeight().width(1.dp).background(LocalExtraColors.current.border))
                    content()
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
        Modifier.fillMaxSize().zIndex(999f).padding(top = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Toast(
            modifier = Modifier.clip(RoundedCornerShape(8.dp))
                .background(LocalExtraColors.current.warningBackground)
                .border(1.dp, LocalExtraColors.current.warningBorder, RoundedCornerShape(8.dp))
                .padding(16.dp),
            mutableStateOf(true),
            2000,
            onFinished = onFinished
        ) {
            Icon(
                Icons.Default.Warning,
                contentDescription = "warning",
                tint = LocalExtraColors.current.warning,
                modifier = Modifier.padding(end = 8.dp).size(16.dp)
            )
            Text(
                message,
                color = LocalExtraColors.current.warning,
                style = MaterialTheme.typography.caption
            )
        }
    }
}
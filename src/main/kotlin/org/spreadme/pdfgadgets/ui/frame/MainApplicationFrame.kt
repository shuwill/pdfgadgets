package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.ui.toolbar.ActionBar
import org.spreadme.pdfgadgets.ui.toolbar.Toolbar
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel

@Composable
fun MainApplicationFrame(
    toolbarViewModel: ToolbarViewModel,
    frameViewModel: ApplicationFrameViewModel,
    loadProgressViewModel: LoadProgressViewModel,
    content: @Composable RowScope.() -> Unit
) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Toolbar(toolbarViewModel = toolbarViewModel)
        Divider(color = LocalExtraColors.current.border)
        Box(Modifier.fillMaxSize()) {
            val progressState = remember { loadProgressViewModel }
            when (progressState.status) {
                LoadProgressStatus.LOADING -> LoadingModal()
                LoadProgressStatus.FAILURE -> FailureModal(progressState.message)
                LoadProgressStatus.SUCCESSFUL -> progressState.onSuccess()
                else -> {}
            }
            Box(Modifier.fillMaxSize().run {
                if (progressState.status != LoadProgressStatus.NONE) {
                    this.blur(8.dp)
                } else {
                    this
                }
            }) {
                Row(Modifier.fillMaxSize()) {
                    ActionBar(frameViewModel, progressState)
                    Box(
                        Modifier.fillMaxHeight().width(1.dp)
                            .background(LocalExtraColors.current.border)
                    )
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
            .background(MaterialTheme.colors.background.copy(alpha = 0.65f))
            .zIndex(999f),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun FailureModal(
    message: String
) {
    Box(
        Modifier
            .fillMaxWidth(0.5f)
            .background(LocalExtraColors.current.errorBackground)
            .border(1.dp, color = LocalExtraColors.current.error)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth().height(32.dp).padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    message,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body1
                )
            }
            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {

                    }
                ) {
                    Text("确定")
                }
            }
        }
    }
}
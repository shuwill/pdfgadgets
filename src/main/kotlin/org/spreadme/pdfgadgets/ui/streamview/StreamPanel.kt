package org.spreadme.pdfgadgets.ui.streamview

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.ui.common.LoadProgressIndicator
import org.spreadme.pdfgadgets.ui.common.Toast
import org.spreadme.pdfgadgets.ui.common.ToastType
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors

@Composable
fun StreamPanel(
    streamPanelViewModel: StreamPanelViewModel
) {
    Column(Modifier.fillMaxSize()) {
        Divider(color = LocalExtraColors.current.border, thickness = 1.dp)
        ActionToolbar {
            streamPanelViewModel.enabled = false
        }
        Divider(color = LocalExtraColors.current.border, thickness = 1.dp)

        LaunchedEffect(streamPanelViewModel.streamUIState) {
            streamPanelViewModel.parse()
        }

        if (streamPanelViewModel.finished) {
            streamPanelViewModel.streamUIState?.let {
                Column(Modifier.fillMaxSize()) {
                    when (it.streamPanelViewType) {
                        StreamPanelViewType.SIGCONTENT -> {
                            StreamASN1View(it as StreamASN1UIState)
                        }
                        StreamPanelViewType.IMAGE -> {
                            StreamImageView(it as StreamImageUIState)
                        }
                        else -> {
                            StreamTextView(it as StreamTextUIState)
                        }
                    }
                }
            }

            streamPanelViewModel.message?.let {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Toast(
                        it,
                        ToastType.ERROR,
                        -1L
                    )
                }
            }
        } else {
            LoadProgressIndicator(Modifier.fillMaxSize(), color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
private fun ActionToolbar(
    onClose: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(32.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.size(16.dp).clickable {
                onClose()
            }
        )
    }
}

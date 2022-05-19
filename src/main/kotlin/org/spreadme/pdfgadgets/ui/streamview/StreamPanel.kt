package org.spreadme.pdfgadgets.ui.streamview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.spreadme.pdfgadgets.ui.common.Toast
import org.spreadme.pdfgadgets.ui.common.ToastType

@Composable
fun StreamPanel(
    streamPanelViewModel: StreamPanelViewModel
) {
    Column(Modifier.fillMaxSize()) {

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
        }
    }
}
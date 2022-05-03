package org.spreadme.pdfgadgets.ui.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.spreadme.pdfgadgets.common.AbstractComponent

class LoadProgressComponent(
    private val progressViewModel: LoadProgressViewModel
) : AbstractComponent("Loading....") {

    @Composable
    override fun doRender() {
        val viewModelState = remember { progressViewModel }
        Column(Modifier.fillMaxSize()) {
            if (viewModelState.loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (viewModelState.successful) {
                viewModelState.onSuccess()
            } else {
                viewModelState.message?.let {
                    Text(it)
                }
            }
        }
    }
}
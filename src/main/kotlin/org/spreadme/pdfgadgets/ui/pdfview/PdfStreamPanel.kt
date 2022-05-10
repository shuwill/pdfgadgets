package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.ui.common.LoadProgressIndicator
import org.spreadme.pdfgadgets.ui.common.clickable

@Composable
fun StructureDetailPanel(
    pdfStreamViewModel: PdfStreamViewModel
) {
    Column(Modifier.fillMaxSize()) {
        ToolBar {
            pdfStreamViewModel.enabled = false
        }
        LoadIndicator(pdfStreamViewModel)
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            PdfStreamDetail(pdfStreamViewModel)
        }
    }
}

@Composable
private fun ToolBar(
    onClose: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(32.dp).background(MaterialTheme.colors.primaryVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "close the structure detail panel",
            tint = MaterialTheme.colors.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp).size(16.dp).clickable {
                onClose()
            }
        )
    }
}

@Composable
private fun LoadIndicator(viewModel: PdfStreamViewModel) {
    if (!viewModel.finished) {
        LoadProgressIndicator(Modifier.fillMaxSize())
    }
}


@Composable
private fun PdfStreamDetail(viewModel: PdfStreamViewModel) {
    val keywordColor = MaterialTheme.colors.primary
    var errorMessage by remember { mutableStateOf("") }
    LaunchedEffect(viewModel.uid) {
        viewModel.stream(keywordColor) { exception ->
            exception.printStackTrace()
            errorMessage = exception.message.toString()
        }
    }
    if (viewModel.finished) {
        if (viewModel.streamContent.isNotEmpty()) {
            LazyColumn(Modifier.fillMaxSize()) {
                items(viewModel.streamContent) { item ->
                    Text(
                        item,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground
                    )
                }
            }
        } else if(viewModel.bufferImage != null) {
            viewModel.bufferImage?.let {
                Image(
                    it.toPainter(),
                    contentDescription = ""
                )
            }
        }
        if (errorMessage.isNotBlank()) {
            Text(
                errorMessage,
                style = MaterialTheme.typography.body2,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}
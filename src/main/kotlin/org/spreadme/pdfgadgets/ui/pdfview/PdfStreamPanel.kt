package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.ui.common.FileDialog
import org.spreadme.pdfgadgets.ui.common.FileDialogMode
import org.spreadme.pdfgadgets.ui.common.LoadProgressIndicator
import org.spreadme.pdfgadgets.ui.common.clickable
import java.awt.image.BufferedImage
import java.nio.file.Path

@Composable
fun StructureDetailPanel(
    pdfStreamViewModel: PdfStreamViewModel
) {
    Column(Modifier.fillMaxSize()) {
        ToolBar(
            pdfStreamViewModel,
            pdfStreamViewModel::onClose,
            pdfStreamViewModel::onSave
        )
        if (!pdfStreamViewModel.finished) {
            LoadProgressIndicator(Modifier.fillMaxSize())
        }
        Column(Modifier.fillMaxSize().padding(8.dp)) {
            PdfStreamDetail(pdfStreamViewModel)
        }
    }
}

@Composable
private fun ToolBar(
    viewModel: PdfStreamViewModel,
    onClose: () -> Unit,
    onSave: (Path) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().height(32.dp).background(MaterialTheme.colors.primaryVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (viewModel.pdfImageInfo != null) {
                Icon(
                    Icons.Default.Download,
                    contentDescription = "download the pdf image",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp).size(16.dp).clickable {
                        FileDialog(
                            viewModel.composeWindow,
                            "保存PDF图片",
                            mode = FileDialogMode.SAVE,
                            exts = arrayListOf(viewModel.pdfImageInfo?.imageType ?: ""),
                            onFileSave = {
                                onSave(it)
                            }
                        )
                    }
                )
                Text(
                    viewModel.pdfImageInfo?.imageType ?: "",
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
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
                    SelectionContainer {
                        Text(
                            item,
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.onBackground
                        )
                    }
                }
            }
        } else if (viewModel.pdfImageInfo != null) {
            viewModel.pdfImageInfo?.let {
                PdfImageDetail(Modifier.wrapContentSize(), it.bufferedImage)
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

@Composable
private fun PdfImageDetail(
    modifier: Modifier = Modifier,
    bufferedImage: BufferedImage
) {
    Box(modifier) {
        Image(
            bufferedImage.toPainter(),
            contentDescription = ""
        )
    }
}
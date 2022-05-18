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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import org.spreadme.pdfgadgets.ui.common.FileDialog
import org.spreadme.pdfgadgets.ui.common.FileDialogMode
import org.spreadme.pdfgadgets.ui.common.clickable
import java.nio.file.Path

@Composable
fun StructureDetailPanel(
    pdfObjectViewModel: PdfObjectViewModel
) {
    Column(Modifier.fillMaxSize()) {
        ToolBar(
            pdfObjectViewModel,
            pdfObjectViewModel::onClose,
            pdfObjectViewModel::onSave
        )
        val keywordColor = MaterialTheme.colors.primary
        LaunchedEffect(pdfObjectViewModel.uid) {
            pdfObjectViewModel.parse(keywordColor)
        }
        if (pdfObjectViewModel.finished) {
            Column(Modifier.fillMaxSize()) {
                PdfStreamDetail(pdfObjectViewModel)
            }
        }
    }
}

@Composable
private fun ToolBar(
    viewModel: PdfObjectViewModel,
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
private fun PdfStreamDetail(viewModel: PdfObjectViewModel) {
    if (viewModel.finished) {
        if (viewModel.annotatedStrings.isNotEmpty()) {
            LazyColumn(Modifier.fillMaxSize().padding(8.dp)) {
                items(viewModel.annotatedStrings) { item ->
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
                it.bufferedImage?.let { bufferedImage ->
                    PdfImageDetail(Modifier.wrapContentSize(), bufferedImage.toPainter())
                }
                it.imageBytes?.let { imageBytes ->
                    PdfImageDetail(
                        Modifier.wrapContentSize(),
                        BitmapPainter(Image.makeFromEncoded(imageBytes).toComposeImageBitmap())
                    )

                }
            }
        } else if (viewModel.asn1Node != null) {
            ASN1StructureTree(viewModel.asn1Node!!, viewModel.sidePanelUIState)
        }
        viewModel.errorMessage?.let {
            if (it.isNotBlank()) {
                Text(
                    it,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
    }
}

@Composable
private fun PdfImageDetail(
    modifier: Modifier = Modifier,
    painter: Painter
) {
    Box(modifier.padding(8.dp)) {
        Image(
            painter,
            contentDescription = ""
        )
    }
}
package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import org.spreadme.pdfgadgets.model.DocumentInfo
import org.spreadme.pdfgadgets.ui.common.Dialog
import org.spreadme.pdfgadgets.ui.common.TextInputField
import org.spreadme.pdfgadgets.ui.common.Tipable
import org.spreadme.pdfgadgets.utils.format

@Composable
fun DocumentAttrDetail(
    title: String,
    documentInfo: DocumentInfo,
    onClose: () -> Unit
) {
    var enabled by remember { mutableStateOf(true) }
    if (enabled) {
        Dialog(
            onClose = {
                enabled = false
                onClose()
            },
            title = title,
            resizable = false,
            state = rememberDialogState(width = 640.dp, height = 388.dp)
        ) {
            DocumentAttrRow("标题", documentInfo.title, true)
            DocumentAttrRow("作者", documentInfo.author, true)
            DocumentAttrRow("主题", documentInfo.subject, true)
            DocumentAttrRow("关键字", documentInfo.keywords, true)
            DocumentAttrRow("修改时间", documentInfo.modDate?.format() ?: "", false)
            DocumentAttrRow("创建时间", documentInfo.creationDate?.format() ?: "", false)
            DocumentAttrRow("PDF版本号", documentInfo.version, false)
            DocumentAttrRow("制作程序", documentInfo.producer, false)
        }
    }
}

@Composable
fun DocumentAttrRow(
    title: String,
    content: String,
    enditable: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(42.dp).padding(horizontal = 32.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(0.2f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = title,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onBackground
            )
        }

        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (enditable) {
                TextInputField(
                    content,
                    modifier = Modifier.fillMaxSize()
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colors.surface)
                        .padding(start = 8.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.caption.copy(color = MaterialTheme.colors.onSurface),
                ) {

                }
            } else {
                Tipable(content) {
                    Text(
                        text = content,
                        textAlign = TextAlign.Start,
                        style = MaterialTheme.typography.caption,
                        color = MaterialTheme.colors.onBackground,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
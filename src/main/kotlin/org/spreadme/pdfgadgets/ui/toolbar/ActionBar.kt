package org.spreadme.pdfgadgets.ui.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.FileDialog
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.pdfview.PdfViewComponent
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors

@Composable
fun ActionBar(
    frameViewModel: ApplicationFrameViewModel
) {
    Column(
        Modifier.fillMaxHeight().width(56.dp)
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ActionIcon(Modifier.padding(top = 16.dp), R.Icons.open) {
            FileDialog(
                parent = frameViewModel.composeWindow,
                title = "打开文件",
                exts = arrayListOf("pdf"),
                onFileOpen = {
                    frameViewModel.openTab(PdfViewComponent(it, frameViewModel))
                }
            )
        }
        ActionIcon(Modifier.padding(top = 16.dp), R.Icons.newfile) {

        }

        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ActionIcon(Modifier.padding(bottom = 16.dp), R.Icons.lignt) {
                frameViewModel.isDark = !frameViewModel.isDark
            }
        }
    }
}

@Composable
fun ActionIcon(
    modifier: Modifier = Modifier,
    resource: String,
    onAction: () -> Unit
) {
    Box(
        modifier.size(32.dp).clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.primary)
            .selectable(true) {
                onAction()
            }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(resource),
            contentDescription = "",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}
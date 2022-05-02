package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.FileDialog
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.pdfview.PdfViewComponent
import org.spreadme.pdfgadgets.ui.toolbar.Toolbar
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel

class HomeComponent(
    private val frameViewModel: ApplicationFrameViewModel,
    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(false)
) : AbstractComponent("新建标签") {

    @Composable
    override fun doRender() {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            println("home component [$uid] rendered")
            Toolbar(toolbarViewModel = toolbarViewModel)
            Column(Modifier.fillMaxSize()) {
                HomeBar(
                    Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
                        .fillMaxWidth().height(48.dp)
                        .background(MaterialTheme.colors.background)
                )
            }
        }
    }

    @Composable
    fun HomeBar(
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = {
                    FileDialog(
                        parent = frameViewModel.composeWindow,
                        title = "打开文件",
                        exts = arrayListOf("pdf"),
                        onFileOpen = {
                            frameViewModel.openTab(PdfViewComponent(it))
                        }
                    )
                }
            ) {
                Icon(
                    painter = painterResource(R.Icons.open),
                    contentDescription = "Open File",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    "打开文件",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.ui.toolbar.ActionBar
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
            Divider(color = LocalExtraColors.current.border)
            Row(Modifier.fillMaxSize()) {
                ActionBar(frameViewModel)
                Box(Modifier.fillMaxHeight().width(1.dp).background(LocalExtraColors.current.border))
            }
        }
    }
}
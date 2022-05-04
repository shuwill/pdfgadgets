package org.spreadme.pdfgadgets.ui.home

import androidx.compose.runtime.Composable
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.frame.LoadProgressViewModel
import org.spreadme.pdfgadgets.ui.frame.MainApplicationFrame
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel

class HomeComponent(
    private val frameViewModel: ApplicationFrameViewModel,
    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(false),
    private val loadProgressViewModel: LoadProgressViewModel = LoadProgressViewModel()
) : AbstractComponent("新建标签") {

    @Composable
    override fun doRender() {
        MainApplicationFrame(
            toolbarViewModel,
            frameViewModel,
            loadProgressViewModel
        ) {
            println("home component【${uid}】rendered")
        }
    }
}
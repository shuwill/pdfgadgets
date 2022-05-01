package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.toolbar.Toolbar
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel

class HomeComponent(
    private val frameViewModel: ApplicationFrameViewModel,
    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(false)
) : AbstractComponent("新建标签") {

    @Composable
    override fun doRender() {
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.Top
        ) {
            println("home component [$uid] rendered")
            Toolbar(toolbarViewModel = toolbarViewModel)
        }
    }

}
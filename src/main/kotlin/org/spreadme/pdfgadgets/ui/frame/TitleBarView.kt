package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.spreadme.compose.window.DecoratedWindowScope
import org.spreadme.compose.window.TitleBar
import org.spreadme.compose.window.newFullscreenControls

@Composable
fun DecoratedWindowScope.TitleBarView(applicationViewModel: ApplicationViewModel) {
    TitleBar(Modifier.newFullscreenControls(), gradientStartColor = applicationViewModel.projectColor) {
        Row(Modifier.align(Alignment.Start).padding(start = 12.dp)) {

        }
    }
}

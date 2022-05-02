package org.spreadme.pdfgadgets.ui.side

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.common.ViewModel

class SideViewModel: ViewModel {

    val expandedMinSize: Dp = 300.dp
    var expandedSize by mutableStateOf(320.dp)
    var isResizing by mutableStateOf(false)
    var isResizeEnable by mutableStateOf(true)
    var verticalScroll by mutableStateOf(0)

    override fun clear() {
    }

}
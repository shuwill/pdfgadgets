package org.spreadme.pdfgadgets.ui.toolbars

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode

class ToolbarsViewModel(
    enabled: Boolean = true
) : ViewModel() {

    companion object {
        val SCALES = arrayListOf("100%", "150%", "200%", "80%", "50%")
    }

    var enabled by mutableStateOf(enabled)
    var scale by mutableStateOf(100)
    var searchKeyword by mutableStateOf("")

    var onChangeSideViewMode: (sidePanelMode: SidePanelMode) -> Unit = {}
    var onChangeScale: (scale: Float) -> Unit = {}

    fun changeSideViewMode(sidePanelMode: SidePanelMode) {
        onChangeSideViewMode(sidePanelMode)
    }

    fun changeScale(type: ScaleType) {
        when (type) {
            ScaleType.ZOOM_IN -> {
                this.scale -= 10
                if (this.scale <= 10) {
                    this.scale = 10
                }
            }
            ScaleType.ZOOM_OUT -> {
                this.scale += 10
                if (this.scale >= 200) {
                    this.scale = 200
                }
            }
        }
        onChangeScale(this.scale / 100.0f)
    }

    fun changeScale(scale: Int) {
        this.scale = scale
        onChangeScale(this.scale / 100.0f)
    }
}
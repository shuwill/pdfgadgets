package org.spreadme.pdfgadgets.ui.toolbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode

class ToolbarViewModel(
    enabled: Boolean = true
) : ViewModel {

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

    fun changeScale(type: ScaleType?, scale: Int = 100) {
        when (type) {
            ScaleType.ZOOM_IN -> {
                this.scale -= 10
                if (this.scale <= 10) {
                    this. scale = 10
                }
            }
            ScaleType.ZOOM_OUT -> {
                this.scale += 10
                if (this.scale >= 200) {
                    this.scale = 200
                }
            }
            else -> {
                this.scale = scale
            }
        }
        onChangeScale(this.scale / 100.0f)
    }

    override fun clear() {

    }
}
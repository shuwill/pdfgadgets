package org.spreadme.pdfgadgets.ui.toolbar

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.ViewModel

class ToolbarViewModel(
    enabled: Boolean = true
) : ViewModel {

    companion object {
        val SCALES = arrayListOf("100%", "150%", "200%", "80%", "50%")
    }

    var enabled by mutableStateOf(enabled)

    var scale by mutableStateOf(100)
    var searchKeyword by mutableStateOf("")

    fun onChangeViewMode(viewMode: ViewMode) {
        //TODO
    }

    fun onChangeScale(type: ScaleType) {
        when (type) {
            ScaleType.ZOOM_IN -> {
                scale -= 10
                if (scale <= 10) {
                    scale = 10
                }
            }
            ScaleType.ZOOM_OUT -> {
                scale += 10
                if (scale >= 200) {
                    scale = 200
                }
            }
        }
    }
}
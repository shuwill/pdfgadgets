package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.PdfMetadata
import org.spreadme.pdfgadgets.model.Position
import org.spreadme.pdfgadgets.ui.side.SideViewMode
import org.spreadme.pdfgadgets.ui.side.SideViewModel

class PdfViewModel(
    val pdfMetadata: PdfMetadata
) : ViewModel {

    var scale by mutableStateOf(1.0f)
    var scrollable by mutableStateOf(false)
    var scrollIndex by mutableStateOf(0)
    var scrollOffset by mutableStateOf(0)

    val searchedPositions : SnapshotStateList<Position> = mutableStateListOf()

    var scrollFinish : () -> Unit = {}

    private var sideViewModes = mutableStateListOf<SideViewMode>()
    private val sideViewModels = mutableStateMapOf<SideViewMode, SideViewModel>()
    private val excludeModesMap = mutableMapOf<SideViewMode, ArrayList<SideViewMode>>()

    init {
        excludeModesMap[SideViewMode.OUTLINES] = arrayListOf(SideViewMode.STRUCTURE, SideViewMode.SIGNATURE)
        excludeModesMap[SideViewMode.STRUCTURE] = arrayListOf(SideViewMode.OUTLINES, SideViewMode.SIGNATURE)
        excludeModesMap[SideViewMode.SIGNATURE] = arrayListOf(SideViewMode.OUTLINES, SideViewMode.STRUCTURE)

        SideViewMode.values().forEach {
            sideViewModels[it] = SideViewModel()
        }
    }

    fun onChangeSideViewMode(sideViewMode: SideViewMode) {
        if (sideViewModes.contains(sideViewMode)) {
            sideViewModes.remove(sideViewMode)
        } else if (!sideViewModes.contains(sideViewMode)) {
            sideViewModes.add(sideViewMode)
            sideViewModes.removeAll(excludeModesMap[sideViewMode] ?: arrayListOf())
        }
    }

    fun sideViewModel(viewMode: SideViewMode): SideViewModel {
        val sideViewState = sideViewModels[viewMode]
        if (sideViewState != null) {
            return sideViewState
        }
        return SideViewModel()
    }

    fun hasSideView(viewMode: SideViewMode): Boolean = sideViewModes.contains(viewMode)

    fun onChangeScalue(scale: Float) {
        println("scale change to $scale")
        this.scale = scale
    }

    fun onScroll(position: Position, scrollFinish: () -> Unit) {
        val offset = position.calculateScrollOffset()
        if(offset == Position.DISABLE) {
            return
        }
        this.scrollable = true
        this.scrollIndex = position.index - 1
        this.scrollOffset = offset
        this.scrollFinish = scrollFinish
    }

    override fun clear() {
        pdfMetadata.close()
    }
}
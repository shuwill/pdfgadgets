package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.PdfMetadata
import org.spreadme.pdfgadgets.model.Position
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelViewModel

class PdfViewModel(
    val pdfMetadata: PdfMetadata
) : ViewModel {

    var scale by mutableStateOf(1.0f)

    var initScrollIndex = 0
    var initScrollOffset = 0
    var horizontalInitScollIndex = 0

    var scrollable by mutableStateOf(false)
    var scrollIndex by mutableStateOf(initScrollIndex)
    var scrollOffset by mutableStateOf(initScrollOffset)

    var scrollFinish : () -> Unit = {}

    val searchedPositions : SnapshotStateList<Position> = mutableStateListOf()

    private var sidePanelModes = mutableStateListOf<SidePanelMode>()
    private val sidePanelModels = mutableStateMapOf<SidePanelMode, SidePanelViewModel>()
    private val excludeModesMap = mutableMapOf<SidePanelMode, ArrayList<SidePanelMode>>()

    init {
        excludeModesMap[SidePanelMode.OUTLINES] = arrayListOf(SidePanelMode.STRUCTURE, SidePanelMode.SIGNATURE)
        excludeModesMap[SidePanelMode.STRUCTURE] = arrayListOf(SidePanelMode.OUTLINES, SidePanelMode.SIGNATURE)
        excludeModesMap[SidePanelMode.SIGNATURE] = arrayListOf(SidePanelMode.OUTLINES, SidePanelMode.STRUCTURE)

        SidePanelMode.values().forEach {
            sidePanelModels[it] = SidePanelViewModel()
        }
    }

    fun onChangeSideViewMode(sidePanelMode: SidePanelMode) {
        if (sidePanelModes.contains(sidePanelMode)) {
            sidePanelModes.remove(sidePanelMode)
        } else if (!sidePanelModes.contains(sidePanelMode)) {
            sidePanelModes.add(sidePanelMode)
            sidePanelModes.removeAll(excludeModesMap[sidePanelMode] ?: arrayListOf())
        }
    }

    fun sideViewModel(viewMode: SidePanelMode): SidePanelViewModel {
        val sideViewState = sidePanelModels[viewMode]
        if (sideViewState != null) {
            return sideViewState
        }
        return SidePanelViewModel()
    }

    fun hasSideView(viewMode: SidePanelMode): Boolean = sidePanelModes.contains(viewMode)

    fun onChangeScalue(scale: Float) {
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
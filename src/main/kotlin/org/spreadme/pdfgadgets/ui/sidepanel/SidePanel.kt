package org.spreadme.pdfgadgets.ui.sidepanel

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import java.awt.Cursor

@Composable
fun SidePanel(
    sidePanelUIState: SidePanelUIState,
    content: @Composable BoxScope.(SidePanelUIState) -> Unit
) {

    val sideViewState = remember { sidePanelUIState }

    Column(
        modifier = Modifier.fillMaxHeight().width(sideViewState.expandedSize)
            .background(LocalExtraColors.current.sidePanelBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content(sidePanelUIState)
            Box(
                modifier = Modifier.fillMaxSize().zIndex(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                SidePanelVerticalSplitter(
                    sideViewState,
                    onResize = {
                        sideViewState.expandedSize = (sideViewState.expandedSize + it)
                            .coerceAtLeast(sideViewState.expandedMinSize)
                    }
                )
            }
        }
    }
}

@Composable
fun SidePanelVerticalSplitter(
    sideViewState: SidePanelUIState,
    onResize: (delta: Dp) -> Unit,
) {
    val density = LocalDensity.current
    Box(
        Modifier.width(8.dp).fillMaxHeight().run {
            if (sideViewState.isResizeEnable) {
                this.draggable(
                    state = rememberDraggableState {
                        with(density) {
                            onResize(it.toDp())
                        }
                    },
                    orientation = Orientation.Horizontal,
                    startDragImmediately = true,
                    onDragStarted = { sideViewState.isResizing = true },
                    onDragStopped = { sideViewState.isResizing = false }
                ).pointerHoverIcon(PointerIcon(Cursor(Cursor.W_RESIZE_CURSOR)))
            } else {
                this
            }
        }
    )
    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(LocalExtraColors.current.border))
}
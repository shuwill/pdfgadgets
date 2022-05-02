package org.spreadme.pdfgadgets.ui.side

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
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
import java.awt.Cursor

@Composable
fun SideView(
    sideViewModel: SideViewModel,
    content: @Composable BoxScope.(SideViewModel) -> Unit
) {

    val sideViewState = remember { sideViewModel }

    Column(
        modifier = Modifier.fillMaxHeight().width(sideViewState.expandedSize)
            .background(MaterialTheme.colors.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            content(sideViewModel)
            Box(
                modifier = Modifier.fillMaxSize().zIndex(1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                VerticalSplitter(
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
fun VerticalSplitter(
    sideViewState: SideViewModel,
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
    Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(MaterialTheme.colors.primaryVariant))
}
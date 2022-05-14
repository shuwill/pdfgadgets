package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.consumeDownChange
import androidx.compose.ui.input.pointer.consumePositionChange
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.model.*
import org.spreadme.pdfgadgets.ui.common.gesture.dragMotionEvent
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import java.awt.Cursor
import kotlin.math.abs

@Composable
fun PageDetail(
    pageViewModel: PageViewModel,
    pdfViewModel: PdfViewModel
) {
    if (pageViewModel.page.enabled.value) {
        Box(modifier = Modifier.padding(start = 0.dp, 24.dp).fillMaxSize()) {
            // mediabox
            mediabox(pageViewModel.page, pdfViewModel.scale) {
                // pdf signature
                signature(pageViewModel.page, pdfViewModel.scale)
                // page view
                AsyncPage(
                    pdfViewModel.viewType,
                    pageViewModel.page,
                    pdfViewModel.scale
                )
                // searched text
                searchedText(
                    pageViewModel.page,
                    pageViewModel.searchPosition,
                    pdfViewModel.scale
                )
            }
        }
    }
}

@Composable
fun mediabox(
    page: PageMetadata,
    scale: Float,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // mediabox size
    Rectangle(
        modifier = Modifier.background(MaterialTheme.colors.background.copy(0.65f))
            .border(1.dp, color = LocalExtraColors.current.border),
        page.mediabox,
        page.mediabox.height,
        scale = scale,
        content = content
    )
}

@Composable
fun BoxScope.AsyncPage(
    viewType: PdfViewType,
    page: PageMetadata,
    scale: Float
) {
    val pageRenderInfo by produceState<PageRenderInfo?>(null) {
        value = page.render(2.0f)
    }

    if (pageRenderInfo != null) {
        Image(
            painter = pageRenderInfo!!.pageImage.toPainter(),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.matchParentSize()
        )

        if (viewType == PdfViewType.TEXT_SELECT) {
            Texts(
                page,
                pageRenderInfo!!.textRenderInfos,
                scale,
                MaterialTheme.colors.primary
            )
        } else if (viewType == PdfViewType.DRAW) {
            // draw area
            drawArea()
        }
    }
}

@Composable
fun Texts(
    page: PageMetadata,
    textRenderInfos: List<TextRenderInfo>,
    scale: Float,
    tint: Color
) {
    var startOffset by remember { mutableStateOf(Offset.Infinite) }
    var endOffset by remember { mutableStateOf(Offset.Infinite) }
    Canvas(
        Modifier.fillMaxSize()
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.TEXT_CURSOR)))
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    startOffset = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDragEnd = { pointerInputChange ->
                    endOffset = pointerInputChange.position
                    pointerInputChange.consumeDownChange()
                }
            )
    ) {
        textRenderInfos.forEach {
            val rectangle = it.position.rectangle
            Rectangle(
                modifier = Modifier.border(1.dp, tint),
                rectangle,
                page.mediabox.height,
                scale = scale
            )

            drawRect(
                color = tint,
                topLeft = Offset(
                    x = (rectangle.x.dp.toPx() * scale),
                    y = ((page.mediabox.height.dp.toPx() - rectangle.y.dp.toPx() - rectangle.height.dp.toPx()) * scale)
                ),
                size = Size(
                    width = (rectangle.width.dp.toPx() * scale),
                    height = (rectangle.height.dp.toPx() * scale)
                ),
                style = Stroke(1f)
            )
        }
    }
}

@Composable
fun signature(
    page: PageMetadata,
    scale: Float
) {
    val showSignature = remember { mutableStateOf(false) }
    val currentSignInfo = remember { mutableStateOf<Signature?>(null) }
    page.signatures.forEach {
        it.position?.let { position ->
            Rectangle(
                modifier = Modifier.zIndex(1f).clickable {
                    showSignature.value = true
                    currentSignInfo.value = it
                },
                position.rectangle,
                page.mediabox.height,
                selectable = position.selected,
                scale = scale
            ) {
                val stroke = Stroke(
                    width = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
                val color = MaterialTheme.colors.secondary
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRoundRect(color = color, style = stroke)
                }
            }
        }
    }
    currentSignInfo.value?.let {
        SignatureDetail(it, showSignature)
    }
}

@Composable
fun searchedText(
    page: PageMetadata,
    positiones: List<Position>,
    scale: Float
) {
    val positionesState = remember { positiones }
    positionesState.forEach {
        Rectangle(
            modifier = Modifier.zIndex(2f),
            it.rectangle,
            page.mediabox.height,
            initColor = MaterialTheme.colors.primary.copy(0.2f),
            selectable = it.selected,
            selectColor = MaterialTheme.colors.secondary,
            scale = scale
        )
    }
}

@Composable
fun drawArea() {
    var offset by remember { mutableStateOf(Offset.Infinite) }
    var size by remember { mutableStateOf(Size.Zero) }
    val color = MaterialTheme.colors.secondary
    Canvas(
        modifier = Modifier.fillMaxSize()
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.CROSSHAIR_CURSOR)))
            .dragMotionEvent(
                onDragStart = { pointerInputChange ->
                    size = Size.Zero
                    offset = pointerInputChange.position
                    pointerInputChange.consumeDownChange()

                },
                onDrag = { pointerInputChange ->
                    val position = pointerInputChange.position
                    size = Size(
                        abs(position.x - offset.x),
                        abs(position.y - offset.y)
                    )
                    pointerInputChange.consumePositionChange()

                },
                onDragEnd = { pointerInputChange ->
                    pointerInputChange.consumeDownChange()
                }
            )
    ) {
        if (!size.isEmpty()) {
            drawRect(
                color = color.copy(0.4f),
                topLeft = offset,
                size = size
            )
        }

    }
}
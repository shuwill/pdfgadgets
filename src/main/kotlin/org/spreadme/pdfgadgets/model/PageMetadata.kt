package org.spreadme.pdfgadgets.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.itextpdf.kernel.geom.Rectangle
import org.spreadme.pdfgadgets.repository.PdfRenderer
import java.awt.image.BufferedImage

class PageMetadata(
    val index: Int,
    val pageSize: Rectangle,
    var mediabox: Rectangle,
    var renderer: PdfRenderer,
    val signatures: List<Signature> = listOf(),
    var enabled: MutableState<Boolean> = mutableStateOf(true),
) {

    suspend fun render(dpi: Float): PageRenderInfo {
        return renderer.render(this, dpi)
    }
}

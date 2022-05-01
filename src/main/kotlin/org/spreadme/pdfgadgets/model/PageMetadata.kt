package org.spreadme.pdfgadgets.model

import com.itextpdf.kernel.geom.Rectangle
import org.spreadme.pdfgadgets.repository.PdfRenderer
import java.awt.image.BufferedImage

class PageMetadata(
    val index: Int,
    val pageSize: Rectangle,
    var mediabox: Rectangle,
    var renderer: PdfRenderer,
    val signatures: List<Signature> = listOf(),
) {

    suspend fun loadImage(dpi: Float): BufferedImage {
        return renderer.render(index - 1, dpi)
    }
}

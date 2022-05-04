package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.spreadme.pdfgadgets.model.FileMetadata
import java.awt.image.BufferedImage

class MupdfRenderer(
    fileMetadata: FileMetadata
): PdfRenderer {

    private val mutex = Mutex()
    private val renderDocument = Document.openDocument(fileMetadata.path) as PDFDocument

    override suspend fun render(index: Int, dpi: Float): BufferedImage {
        mutex.withLock {
            var loadPage: Page? = null
            var pixmap: Pixmap? = null
            var drawDevice: DrawDevice? = null
            try {
                loadPage = renderDocument.loadPage(index)
                val scale = Matrix().scale(dpi)
                pixmap = loadPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
                pixmap.clear(255)

                drawDevice = DrawDevice(pixmap)
                loadPage.run(drawDevice, scale)

                val width = pixmap.width
                val height = pixmap.height
                val image = BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB)
                image.setRGB(0, 0, width, height, pixmap.pixels, 0, width)
                return image
            } finally {
                drawDevice?.close()
                drawDevice?.destroy()

                pixmap?.destroy()
                loadPage?.destroy()
            }
        }
    }

    override fun close() {
        renderDocument.destroy()
    }
}
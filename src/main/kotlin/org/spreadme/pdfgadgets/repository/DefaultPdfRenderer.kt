package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.*
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.canvas.parser.PdfCanvasProcessor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.spreadme.pdfgadgets.model.FileMetadata
import org.spreadme.pdfgadgets.model.PageMetadata
import org.spreadme.pdfgadgets.model.PageRenderInfo
import java.awt.image.BufferedImage

class DefaultPdfRenderer(
    fileMetadata: FileMetadata,
    pdfDocument: PdfDocument
): PdfRenderer {

    private val mutex = Mutex()
    private val pageImageRender = Document.openDocument(fileMetadata.path) as PDFDocument
    private val pdfDocument: PdfDocument = pdfDocument

    override suspend fun render(page: PageMetadata, dpi: Float): PageRenderInfo {
        mutex.withLock {
            var loadPage: Page? = null
            var pixmap: Pixmap? = null
            var drawDevice: DrawDevice? = null
            try {
                loadPage = pageImageRender.loadPage(page.index - 1)
                val scale = Matrix().scale(dpi)
                pixmap = loadPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
                pixmap.clear(255)

                drawDevice = DrawDevice(pixmap)
                loadPage.run(drawDevice, scale)

                val width = pixmap.width
                val height = pixmap.height
                val image = BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB)
                image.setRGB(0, 0, width, height, pixmap.pixels, 0, width)

                val strategy = TextRenderEventListener(page)
                val processor = PdfCanvasProcessor(strategy, HashMap())
                processor.processPageContent(pdfDocument.getPage(page.index))
                return PageRenderInfo(image, strategy.textRenderers)
            } finally {
                drawDevice?.close()
                drawDevice?.destroy()

                pixmap?.destroy()
                loadPage?.destroy()
            }
        }
    }

    override fun close() {
        pageImageRender.destroy()
    }
}
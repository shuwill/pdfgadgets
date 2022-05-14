package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.*
import com.itextpdf.kernel.geom.Rectangle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.spreadme.pdfgadgets.model.*
import java.awt.image.BufferedImage

class DefaultPdfRenderer(fileMetadata: FileMetadata): PdfRenderer {

    private val mutex = Mutex()
    private val pageImageRender = Document.openDocument(fileMetadata.path) as PDFDocument

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

                val textBlockPositions = arrayListOf<Position>()
                val textRenderInfos = arrayListOf<TextRenderInfo>()
                val structuredText = loadPage.toStructuredText()
                structuredText.blocks.forEach {block ->
                    val blockRect = block.bbox
                    val textBlockRectangle = Rectangle(blockRect.x0, blockRect.y0, blockRect.x1 - blockRect.x0, blockRect.y1 - blockRect.y0)
                    var blank = true
                    block.lines.forEach { line ->
                        line.chars.forEach {char ->
                            if(!char.isWhitespace){
                                val rect = char.quad.toRect()
                                val rectangle = Rectangle(rect.x0, rect.y0, rect.x1 - rect.x0, rect.y1 - rect.y0)
                                textRenderInfos.add(TextRenderInfo(Char(char.c).toString(),
                                    Position(page.index, page.pageSize, rectangle)))
                                blank = false
                            }
                        }
                    }
                    if(!blank) {
                        textBlockPositions.add(Position(page.index, page.pageSize, textBlockRectangle))
                    }

                }

                return PageRenderInfo(image, textRenderInfos, textBlockPositions)
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
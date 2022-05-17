package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.*
import com.itextpdf.kernel.geom.Rectangle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.spreadme.pdfgadgets.model.*

class DefaultPdfRenderer(fileMetadata: FileMetadata) : PdfRenderer {

    private val mutex = Mutex()
    private val file = fileMetadata
    private val pageImageRender = Document.openDocument(fileMetadata.path) as PDFDocument

    override suspend fun render(page: PageMetadata, dpi: Float): PageRenderInfo {
        val lockKey = "${file.name}-${page.index}"
        mutex.withLock(lockKey) {
            var loadPage: Page? = null
            var pixmap: Pixmap? = null
            var drawDevice: DrawDevice? = null
            try {
                loadPage = pageImageRender.loadPage(page.index - 1)
                // text info
                val textBlocks = page.textBlocks.ifEmpty {
                    val textBlocks = arrayListOf<TextBlock>()
                    val structuredText = loadPage.toStructuredText()
                    structuredText.blocks.forEach { block ->
                        val blockRect = block.bbox
                        val textBlockRectangle = Rectangle(
                            blockRect.x0, blockRect.y0,
                            blockRect.x1 - blockRect.x0,
                            blockRect.y1 - blockRect.y0
                        )
                        val chars = arrayListOf<Char>()
                        var blank = true
                        block.lines.forEach { line ->
                            line.chars.forEach { char ->
                                if (!char.isWhitespace) {
                                    blank = false
                                }
                                chars.add(char.c.toChar())
                            }
                        }
                        if (!blank) {
                            val textBlock = TextBlock(chars, Position(page.index, page.pageSize, textBlockRectangle))
                            textBlocks.add(textBlock)
                        }
                    }
                    page.textBlocks = textBlocks
                    textBlocks
                }

                val scale = Matrix().scale(dpi)
                pixmap = loadPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
                pixmap.clear(255)

                drawDevice = DrawDevice(pixmap)
                loadPage.run(drawDevice, scale)
                val pixmapMetadata = PixmapMetadata(pixmap.width, pixmap.height, pixmap.pixels)

                return PageRenderInfo(pixmapMetadata, textBlocks)
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
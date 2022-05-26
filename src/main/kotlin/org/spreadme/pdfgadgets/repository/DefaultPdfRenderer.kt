package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.*
import com.itextpdf.kernel.geom.Rectangle
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.KotlinLogging
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.model.*
import org.spreadme.pdfgadgets.utils.*
import java.nio.file.Files
import java.nio.file.Paths

class DefaultPdfRenderer(fileMetadata: FileMetadata) : PdfRenderer {

    private val logger = KotlinLogging.logger {}

    private val mutex = Mutex()
    private val file = fileMetadata
    private val pageImageRender = Document.openDocument(fileMetadata.path) as PDFDocument

    init {
        fileMetadata.openProperties.password?.let {
            pageImageRender.authenticatePassword(String(it))
        }
    }

    override suspend fun render(page: PageMetadata, dpi: Float): PageRenderInfo {
        val lockKey = "${file.name}-${page.index}"
        mutex.withLock(lockKey) {
            var loadPage: Page? = null
            var pixmap: Pixmap? = null
            var drawDevice: DrawDevice? = null
            try {
                logger.debug("begin render ${file.name}: page[${page.index}], dpi[$dpi]")
                val start = System.currentTimeMillis()
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

                // create cache index
                val cacheIndex = "${page.index}-$dpi"
                val cachePath = Paths.get(AppConfig.indexPath.toString(), file.uid, cacheIndex)
                val pixels = if (Files.exists(cachePath) && page.pixmapMetadata != null) {
                    toMappedByteBuffer(cachePath).asIntBuffer().toIntArray()
                } else {
                    createFile(cachePath, true)
                    val scale = Matrix().scale(dpi)
                    pixmap = loadPage.toPixmap(scale, ColorSpace.DeviceBGR, true, true)
                    pixmap.clear(255)

                    drawDevice = DrawDevice(pixmap)
                    loadPage.run(drawDevice, scale)
                    page.pixmapMetadata = PixmapMetadata(pixmap.width, pixmap.height)
                    val pixels = pixmap.pixels
                    toFile(cachePath, toByteBuffer(pixels))
                    pixels
                }

                val bufferedImage = page.pixmapMetadata!!.toBufferedImage(pixels)

                val end = System.currentTimeMillis()
                logger.debug("end render ${file.name}: page[${page.index}], dpi[$dpi], cost ${end - start} ms")

                return PageRenderInfo(bufferedImage, textBlocks)
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
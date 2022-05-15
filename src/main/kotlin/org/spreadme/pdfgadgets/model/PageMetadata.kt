package org.spreadme.pdfgadgets.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.itextpdf.kernel.geom.Rectangle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.repository.PdfRenderer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.imageio.ImageIO

class PageMetadata(
    val index: Int,
    val pageSize: Rectangle,
    var mediabox: Rectangle,
    var renderer: PdfRenderer,
    val signatures: List<Signature> = listOf(),
    private var textBlocks: List<TextBlock> = listOf(),
    private var pageImagePath: Path? = null,
    var enabled: MutableState<Boolean> = mutableStateOf(true),
) {

    fun pageImagePath(): Path? = pageImagePath

    suspend fun render(dpi: Float): PageRenderInfo {
        if(textBlocks.isNotEmpty() && pageImagePath != null && Files.exists(pageImagePath!!)) {
            val bufferedImage = withContext(Dispatchers.IO) {
                ImageIO.read(pageImagePath!!.toFile())
            }
            return PageRenderInfo(bufferedImage, textBlocks)
        }
        val pageRenderInfo =  renderer.render(this, dpi)
        textBlocks = pageRenderInfo.textBlocks
        val indexPath = AppConfig.indexPath
        pageImagePath = Paths.get(indexPath.toString(), UUID.randomUUID().toString())
        withContext(Dispatchers.IO) {
            ImageIO.write(pageRenderInfo.pageImage, "PNG", pageImagePath!!.toFile())
        }
        return pageRenderInfo
    }
}

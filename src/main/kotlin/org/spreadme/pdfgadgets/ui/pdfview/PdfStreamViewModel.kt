package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfStream
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.PdfImageInfo
import org.spreadme.pdfgadgets.repository.PdfStreamParser
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO

class PdfStreamViewModel(
    private val pdfStreamParser: PdfStreamParser,
    val composeWindow: ComposeWindow
) : ViewModel() {

    var uid by mutableStateOf(UUID.randomUUID().toString())
    var sidePanelUIState = SidePanelUIState()
    var enabled by mutableStateOf(false)
    var pdfStream by mutableStateOf(PdfStream())

    var pdfImageInfo by mutableStateOf<PdfImageInfo?>(null)
    val streamContent = arrayListOf<AnnotatedString>()

    var finished by mutableStateOf(false)

    suspend fun stream(keywordColor: Color, onError: (Exception) -> Unit) {
        try {
            if (pdfStream[PdfName.Subtype] == PdfName.Image) {
                pdfImageInfo = pdfStreamParser.getImage(pdfStream)
            } else if (pdfStream[PdfName.Subtype] == PdfName.XML) {
                val annotatedString = buildAnnotatedString {
                    append(String(pdfStream.getBytes(true)))
                }
                streamContent.add(annotatedString)
            } else {
                streamContent.addAll(pdfStreamParser.parse(pdfStream, keywordColor))
            }
        } catch (e: Exception) {
            onError(e)
        }
        finished = true
    }

    fun onClose() {
        enabled = false
    }

    fun onSave(path: Path) {
        ImageIO.write(pdfImageInfo?.bufferedImage, pdfImageInfo?.imageType ?: "", path.toFile())
    }

    fun reset() {
        finished = false
        uid = UUID.randomUUID().toString()
        pdfImageInfo = null
        streamContent.clear()
    }
}
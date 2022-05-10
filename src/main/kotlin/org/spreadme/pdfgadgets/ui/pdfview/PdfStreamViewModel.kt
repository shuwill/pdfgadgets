package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfStream
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.repository.PdfStreamParser
import java.awt.image.BufferedImage
import java.util.UUID

class PdfStreamViewModel(
    private val pdfStreamParser: PdfStreamParser
) : ViewModel() {

    var uid by mutableStateOf(UUID.randomUUID().toString())
    var enabled by mutableStateOf(false)
    var pdfStream by mutableStateOf(PdfStream())

    var bufferImage by mutableStateOf<BufferedImage?>(null)
    val streamContent = arrayListOf<AnnotatedString>()

    var finished by mutableStateOf(false)

    suspend fun stream(keywordColor: Color, onError: (Exception) -> Unit) {
        try {
            if (pdfStream[PdfName.Subtype] == PdfName.Image) {
                val pdfImage = PdfImageXObject(pdfStream)
                bufferImage = pdfImage.bufferedImage
            } else if (pdfStream[PdfName.Subtype] == PdfName.XML) {
                println(String(pdfStream.getBytes(true)))
            } else {
                streamContent.addAll(pdfStreamParser.parse(pdfStream, keywordColor))
            }
        } catch (e: Exception) {
            onError(e)
        }
        finished = true
    }

    fun reset() {
        finished = false
        uid = UUID.randomUUID().toString()
        streamContent.clear()
    }
}
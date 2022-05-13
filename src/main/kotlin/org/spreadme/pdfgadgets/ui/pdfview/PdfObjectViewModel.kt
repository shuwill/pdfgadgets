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
import com.itextpdf.kernel.pdf.PdfString
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.ASN1Node
import org.spreadme.pdfgadgets.model.PdfImageInfo
import org.spreadme.pdfgadgets.model.StructureNode
import org.spreadme.pdfgadgets.repository.ASN1Parser
import org.spreadme.pdfgadgets.repository.PdfStreamParser
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import java.io.ByteArrayInputStream
import java.nio.file.Path
import java.util.*
import javax.imageio.ImageIO

class PdfObjectViewModel(
    private val pdfStreamParser: PdfStreamParser,
    private val asN1Parser: ASN1Parser,
    val composeWindow: ComposeWindow
) : ViewModel() {

    var uid by mutableStateOf(UUID.randomUUID().toString())
    var sidePanelUIState = SidePanelUIState()
    var enabled by mutableStateOf(false)
    var structureNode by mutableStateOf<StructureNode?>(null)

    var pdfImageInfo by mutableStateOf<PdfImageInfo?>(null)
    val annotatedStrings = arrayListOf<AnnotatedString>()
    var asn1Node by mutableStateOf<ASN1Node?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    var finished by mutableStateOf(false)

    suspend fun parse(keywordColor: Color) {
        try {
            structureNode?.let { node ->
                if (node.isPdfStream() && node.pdfObject is PdfStream) {
                    if (node.pdfObject[PdfName.Subtype] == PdfName.Image) {
                        pdfImageInfo = pdfStreamParser.getImage(node.pdfObject)
                    } else if (node.pdfObject[PdfName.Subtype] == PdfName.XML) {
                        annotatedStrings.addAll(parseXml(node.pdfObject.getBytes(true)))
                    } else {
                        annotatedStrings.addAll(pdfStreamParser.parse(node.pdfObject, keywordColor))
                    }
                }
                if (node.isSignatureContent() && node.pdfObject is PdfString) {
                    val byteArray = node.pdfObject.valueBytes
                    asn1Node = asN1Parser.parse(byteArray)
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }
        finished = true
    }

    private fun parseXml(byteArray: ByteArray): List<AnnotatedString> =
        ByteArrayInputStream(byteArray).bufferedReader().lines()
            .map { buildAnnotatedString { append(it) } }
            .toList()

    private fun parseString(content: String): List<AnnotatedString> =
        content.byteInputStream().bufferedReader().lines()
            .map { buildAnnotatedString { append(it) } }
            .toList()

    fun onClose() {
        enabled = false
    }

    fun onSave(path: Path) {
        ImageIO.write(pdfImageInfo?.bufferedImage, pdfImageInfo?.imageType ?: "", path.toFile())
    }

    fun reset(node: StructureNode) {
        enabled = true
        finished = false
        uid = UUID.randomUUID().toString()
        this.structureNode = node
        pdfImageInfo = null
        annotatedStrings.clear()
        errorMessage = null
    }
}
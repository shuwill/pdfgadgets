package org.spreadme.pdfgadgets.ui.streamview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.PdfStream
import com.itextpdf.kernel.pdf.PdfString
import kotlinx.coroutines.launch
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.common.viewModelScope
import org.spreadme.pdfgadgets.model.ASN1Node
import org.spreadme.pdfgadgets.model.PdfImageInfo
import org.spreadme.pdfgadgets.model.StructureNode
import org.spreadme.pdfgadgets.repository.ASN1Parser
import org.spreadme.pdfgadgets.repository.PdfStreamParser
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import java.util.*

class StreamPanelViewModel(
    private val streamParser: PdfStreamParser, private val asN1Parser: ASN1Parser
) : ViewModel() {

    var enabled by mutableStateOf(false)
    var finished by mutableStateOf(false)

    var sidePanelUIState = SidePanelUIState()
    var streamUIState: StreamUIState? = null

    var message by mutableStateOf<String?>(null)

    fun parse() {
        viewModelScope.launch {
            try {
                streamUIState?.parse()
            } catch (e: Exception) {
                e.printStackTrace()
                streamUIState = null
                message = e.message ?: "不支持的Stream"
            }
            finished = true
        }
    }

    fun swicth(node: StructureNode) {
        enabled = true
        finished = false
        message = null
        setStreamViewUIState(node)
    }

    private fun setStreamViewUIState(node: StructureNode) {
        if (node.isPdfStream()) {
            val pdfStream = node.pdfObject as PdfStream
            streamUIState = if (pdfStream[PdfName.Subtype] == PdfName.Image) {
                StreamImageUIState(node, streamParser, StreamPanelViewType.IMAGE)
            } else if (pdfStream[PdfName.Subtype] == PdfName.XML) {
                StreamTextUIState(node, streamParser, StreamPanelViewType.XML)
            } else {
                StreamTextUIState(node, streamParser, StreamPanelViewType.DEFAULT)
            }
        } else if (node.isSignatureContent()) {
            streamUIState = StreamASN1UIState(node, asN1Parser, StreamPanelViewType.SIGCONTENT)
        }
    }
}

abstract class StreamUIState(
    val uid: String, val structureNode: StructureNode, val streamPanelViewType: StreamPanelViewType
) {

    abstract suspend fun parse()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StreamUIState

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}

class StreamImageUIState(
    structureNode: StructureNode, private val streamParser: PdfStreamParser, streamPanelViewType: StreamPanelViewType
) : StreamUIState(
    UUID.randomUUID().toString(), structureNode, streamPanelViewType
) {

    var pdfImageInfo by mutableStateOf<PdfImageInfo?>(null)

    override suspend fun parse() {
        pdfImageInfo = streamParser.parseImage(structureNode.pdfObject as PdfStream)
    }
}

class StreamTextUIState(
    structureNode: StructureNode,
    private val streamParser: PdfStreamParser,
    streamPanelViewType: StreamPanelViewType
) : StreamUIState(
    UUID.randomUUID().toString(),
    structureNode,
    streamPanelViewType
) {

    val fontSize by mutableStateOf(13.sp)
    var texts = listOf<AnnotatedString>()

    override suspend fun parse() {
        texts = if (streamPanelViewType == StreamPanelViewType.XML) {
            streamParser.parseXml(structureNode.pdfObject as PdfStream)
        } else {
            streamParser.parse(structureNode.pdfObject as PdfStream)
        }
    }
}

class StreamASN1UIState(
    structureNode: StructureNode, private val asN1Parser: ASN1Parser, streamPanelViewType: StreamPanelViewType
) : StreamUIState(
    UUID.randomUUID().toString(), structureNode, streamPanelViewType
) {

    lateinit var root: ASN1Node
    val sidePanelUIState = SidePanelUIState()

    override suspend fun parse() {
        val pdfString = structureNode.pdfObject as PdfString
        root = asN1Parser.parse(pdfString.valueBytes)
    }

}
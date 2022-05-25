package org.spreadme.pdfgadgets.ui.streamview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.sp
import com.itextpdf.kernel.pdf.PdfObject
import com.itextpdf.kernel.pdf.PdfStream
import kotlinx.coroutines.launch
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.common.viewModelScope
import org.spreadme.pdfgadgets.model.ASN1Node
import org.spreadme.pdfgadgets.model.PdfImageInfo
import org.spreadme.pdfgadgets.repository.ASN1Parser
import org.spreadme.pdfgadgets.repository.PdfStreamParser
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import org.spreadme.pdfgadgets.ui.theme.defaultKeywordColor
import org.spreadme.pdfgadgets.utils.uuid

class StreamPanelViewModel : ViewModel() {

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

    fun swicth(uiState: StreamUIState?) {
        enabled = true
        finished = false
        message = null
        streamUIState = uiState
    }
}

abstract class StreamUIState(
    val uid: String,
    val streamPanelViewType: StreamPanelViewType
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

    private val streamParser: PdfStreamParser,
    private val pdfObject: PdfObject,
    streamPanelViewType: StreamPanelViewType

) : StreamUIState(uuid(), streamPanelViewType) {

    var pdfImageInfo by mutableStateOf<PdfImageInfo?>(null)

    override suspend fun parse() {
        pdfImageInfo = streamParser.parseImage(pdfObject as PdfStream)
    }
}

class StreamTextUIState(

    private val streamParser: PdfStreamParser,
    private val pdfObject: PdfObject,
    streamPanelViewType: StreamPanelViewType

) : StreamUIState(uuid(), streamPanelViewType) {

    val fontSize by mutableStateOf(13.sp)
    var texts = listOf<AnnotatedString>()

    override suspend fun parse() {
        texts = if (streamPanelViewType == StreamPanelViewType.XML) {
            streamParser.parseXml(pdfObject as PdfStream)
        } else {
            streamParser.parse(pdfObject as PdfStream, defaultKeywordColor())
        }
    }
}

class StreamASN1UIState(

    private val asN1Parser: ASN1Parser,
    private val content: ByteArray,
    streamPanelViewType: StreamPanelViewType

) : StreamUIState(uuid(), streamPanelViewType) {

    lateinit var root: ASN1Node
    val sidePanelUIState = SidePanelUIState()

    override suspend fun parse() {
        root = asN1Parser.parse(content)
    }

}
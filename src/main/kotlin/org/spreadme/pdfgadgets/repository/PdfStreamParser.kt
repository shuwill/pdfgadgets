package org.spreadme.pdfgadgets.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.itextpdf.io.source.PdfTokenizer
import com.itextpdf.io.source.RandomAccessFileOrArray
import com.itextpdf.io.source.RandomAccessSourceFactory
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.canvas.parser.util.PdfCanvasParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bouncycastle.util.encoders.Hex

class PdfStreamParser {

    suspend fun parse(pdfStream: PdfStream, keywordColor: Color): List<AnnotatedString> {
        return withContext(Dispatchers.IO) {
            contentStream(pdfStream, keywordColor)
        }
    }

    private fun contentStream(pdfStream: PdfStream, keywordColor: Color): List<AnnotatedString> {
        val annotatedStrings = arrayListOf<AnnotatedString>()
        if (pdfStream[PdfName.Length1] != null) {
            val bytes = pdfStream.getBytes(false)
            val annotatedString = buildAnnotatedString {
                append(String(bytes))
            }
            annotatedStrings.add(annotatedString)
        } else if (pdfStream[PdfName.Length1] == null) {
            val bytes = pdfStream.bytes
            val tokenizer = PdfTokenizer(RandomAccessFileOrArray(RandomAccessSourceFactory().createSource(bytes)))
            val canvasParser = PdfCanvasParser(tokenizer)
            val tokens = arrayListOf<PdfObject>()

            while (canvasParser.parse(tokens).size > 0) {
                // operator is at the end
                val operator = tokens[tokens.size - 1].toString()
                // operands are in front of their operator
                val builder = StringBuilder()
                for (i in 0 until tokens.size - 1) {
                    appendContents(builder, tokens[i])
                }
                val operands = builder.toString()

                val content = buildAnnotatedString {
                    operands.let {
                        if (it.isNotBlank()) {
                            append(it)
                        }
                    }
                    withStyle(style = SpanStyle(color = keywordColor)) {
                        append(operator)
                        append("\n")
                    }
                }
                annotatedStrings.add(content)
            }
        }
        return annotatedStrings
    }

    private fun appendContents(builder: StringBuilder, obj: PdfObject) {
        when (obj.type) {
            PdfObject.STRING -> {
                val pdfString = obj as PdfString
                if (pdfString.isHexWriting) {
                    builder.append("<").append(Hex.toHexString(pdfString.valueBytes)).append(">")
                } else {
                    builder.append("(").append(obj).append(") ")
                }
            }
            PdfObject.DICTIONARY -> {
                val dict = obj as PdfDictionary
                builder.append("<<")
                for (key in dict.keySet()) {
                    builder.append(key).append(" ")
                    appendContents(builder, dict[key, false])
                }
                builder.append(">> ")
            }
            else -> builder.append(obj).append(" ")
        }
    }
}
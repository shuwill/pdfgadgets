package org.spreadme.pdfgadgets.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.itextpdf.kernel.pdf.*
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.model.OperatorName
import org.spreadme.pdfgadgets.model.PdfImageInfo
import org.spreadme.pdfgadgets.ui.theme.StreamKeywordColors
import org.spreadme.pdfgadgets.utils.applyMask
import org.spreadme.pdfgadgets.utils.getArray
import org.spreadme.pdfgadgets.utils.getBoolean
import java.io.ByteArrayInputStream

class PdfStreamParser : KoinComponent {

    private val customPdfCanvasProcessor by inject<CustomPdfCanvasProcessor>()

    suspend fun parse(pdfStream: PdfStream, colotPalette: StreamKeywordColors): List<AnnotatedString> {
        return withContext(Dispatchers.IO) {
            contentStream(pdfStream, colotPalette)
        }
    }

    suspend fun parseXml(pdfStream: PdfStream): List<AnnotatedString> {
        return withContext(Dispatchers.IO) {
            ByteArrayInputStream(pdfStream.getBytes(true)).bufferedReader().lines()
                .map { buildAnnotatedString { append(it) } }
                .toList()
        }
    }

    suspend fun parseImage(pdfStream: PdfStream): PdfImageInfo {
        return withContext(Dispatchers.IO) {
            val pdfImage = PdfImageXObject(pdfStream)

            val softMask = maskImage(pdfStream, PdfName.SMask)
            val mask = maskImage(pdfStream, PdfName.Mask)

            val bufferedImage = if (softMask != null) {
                applyMask(
                    pdfImage.bufferedImage, softMask.bufferedImage,
                    softMask.getBoolean(PdfName.Interpolate, false),
                    true,
                    softMask.getArray(PdfName("MATTE"))
                )
            } else if (mask != null && mask.getBoolean(PdfName.ImageMask, false)) {
                applyMask(
                    pdfImage.bufferedImage, mask.bufferedImage,
                    mask.getBoolean(PdfName.Interpolate, false),
                    true, null
                )
            } else {
                pdfImage.bufferedImage
            }
            PdfImageInfo(bufferedImage, imageType = pdfImage.identifyImageFileExtension())
        }
    }

    private fun contentStream(pdfStream: PdfStream, colotPalette: StreamKeywordColors): List<AnnotatedString> {
        val annotatedStrings = arrayListOf<AnnotatedString>()
        val indentRule = IndentRule(colotPalette = colotPalette)
        if (pdfStream[PdfName.Length1] != null) {
            val bytes = pdfStream.getBytes(false)
            val annotatedString = buildAnnotatedString {
                append(String(bytes))
            }
            annotatedStrings.add(annotatedString)
        } else if (pdfStream[PdfName.Length1] == null) {
            val tokens = customPdfCanvasProcessor.parsePdfStream(pdfStream.bytes)
            var tokenbuilder = AnnotatedString.Builder()

            tokens.forEach {
                if (it is PdfLiteral) {
                    addOperators(it, tokenbuilder, indentRule)
                } else {
                    addOperand(it, tokenbuilder, indentRule)
                }

                if (tokenbuilder.toAnnotatedString().endsWith("\n")) {
                    annotatedStrings.add(tokenbuilder.toAnnotatedString())
                    tokenbuilder = AnnotatedString.Builder()
                }
            }
        }
        return annotatedStrings
    }

    private fun addOperators(literal: PdfLiteral, operators: AnnotatedString.Builder, indentRule: IndentRule) {
        val operator = literal.toString()

        if (operator == OperatorName.END_TEXT || operator == OperatorName.RESTORE
            || operator == OperatorName.END_MARKED_CONTENT
        ) {
            indentRule.indent--
        }
        addIndent(operators, indentRule)

        operators.append(buildAnnotatedString(operator + "\n", indentRule.colotPalette.operator))
        // nested opening operators
        if (operator == OperatorName.BEGIN_TEXT ||
            operator == OperatorName.SAVE ||
            operator == OperatorName.BEGIN_MARKED_CONTENT ||
            operator == OperatorName.BEGIN_MARKED_CONTENT_SEQ
        ) {
            indentRule.indent++
        }
        indentRule.needIndent = true
    }

    private fun addOperand(pdfObject: PdfObject, operands: AnnotatedString.Builder, indentRule: IndentRule) {
        addIndent(operands, indentRule)
        when (pdfObject) {
            is PdfName -> {
                operands.append(buildAnnotatedString("$pdfObject ", indentRule.colotPalette.name))
            }
            is PdfBoolean -> {
                operands.append(buildAnnotatedString("$pdfObject "))
            }
            is PdfArray -> {
                for (elem in pdfObject) {
                    addOperand(elem, operands, indentRule)
                }
            }
            is PdfString -> {
                val bytes = pdfObject.valueBytes
                for (b in bytes) {
                    val chr = b.toInt() and 0xff
                    if (chr == '('.code || chr == ')'.code || chr == '\\'.code) {
                        // PDF reserved characters must be escaped
                        val str = "\\" + chr.toChar()
                        operands.append(buildAnnotatedString(str, indentRule.colotPalette.escape))

                    } else if (chr < 0x20 || chr > 0x7e) {
                        // non-printable ASCII is shown as an octal escape
                        val str = String.format("\\%03o", chr)
                        operands.append(buildAnnotatedString(str, indentRule.colotPalette.escape))

                    } else {
                        val str = chr.toChar().toString()
                        operands.append(buildAnnotatedString(str, indentRule.colotPalette.escape))

                    }
                }
            }
            is PdfNumber -> {
                operands.append(buildAnnotatedString("$pdfObject ", indentRule.colotPalette.number))
            }
            is PdfDictionary -> {
                pdfObject.entrySet().forEach {
                    addOperand(it.key, operands, indentRule)
                    addOperand(it.value, operands, indentRule)
                }
            }
            is PdfNull -> {
                operands.append(buildAnnotatedString("null "))
            }
            else -> {
                operands.append(buildAnnotatedString("$pdfObject "))
            }
        }
    }

    private fun addIndent(indent: AnnotatedString.Builder, indentRule: IndentRule) {
        if (indentRule.needIndent) {
            for (i in 0 until indentRule.indent) {
                indent.append("    ")
            }
            indentRule.needIndent = false
        }
    }

    private fun buildAnnotatedString(text: String, style: Color? = null) = buildAnnotatedString {
        if (style != null) {
            withStyle(style = SpanStyle(style)) {
                append(text)
            }
        } else {
            append(text)
        }
    }

    private fun maskImage(pdfStream: PdfStream, pdfName: PdfName): PdfImageXObject? {
        val pdfObject = pdfStream[pdfName]
        if (pdfObject != null && pdfObject is PdfStream && pdfObject[PdfName.Subtype] == PdfName.Image) {
            return PdfImageXObject(pdfObject)
        }
        return null
    }
}

data class IndentRule(
    var indent: Int = 0,
    var needIndent: Boolean = false,
    val colotPalette: StreamKeywordColors,
)
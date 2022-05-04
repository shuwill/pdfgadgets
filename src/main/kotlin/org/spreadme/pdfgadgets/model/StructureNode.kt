package org.spreadme.pdfgadgets.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.itextpdf.kernel.pdf.*
import org.spreadme.pdfgadgets.utils.new

class StructureNode(
    val pdfName: PdfName?,
    val pdfObject: PdfObject,
    val type: PdfObjectType,
    val level: Int = 1,
    var paths: ArrayList<String> = arrayListOf(),
    var childs: List<StructureNode>  = listOf(),
    val expanded: MutableState<Boolean> = mutableStateOf(false)
) {

    constructor(pdfObject: PdfObject): this(null, pdfObject, 0, arrayListOf())

    constructor(pdfName: PdfName? = null, pdfObject: PdfObject, level: Int = 0, path: ArrayList<String>) : this(
        pdfName,
        pdfObject,
        getPdfObjectType(pdfObject.type.toInt()),
        level,
        path
    ){
        if(pdfName != null) {
            path.add(pdfName.toString())
        } else {
            path.add(type.toString(pdfObject))
        }
    }

    fun hasChild(): Boolean = type.hasChild && pdfName != PdfName.Parent

    fun childs(): List<StructureNode> {
        if(childs.isNotEmpty()) {
            return childs
        }
        if (pdfObject is PdfDictionary && pdfName != PdfName.Parent) {
            childs = pdfObject.entrySet().map { StructureNode(it.key, it.value, level + 1, paths.new()) }.toList()
        } else if (pdfObject is PdfStream) {
            childs = pdfObject.entrySet().map { StructureNode(it.key, it.value, level + 1, paths.new()) }.toList()
        } else if (pdfObject is PdfArray) {
            childs = pdfObject.map { StructureNode(null, it, level + 1, paths.new()) }.toList()
        }
        return childs
    }

    fun isPageContents() = paths.contains(PdfName.Page.toString()) && paths.last() == PdfName.Contents.toString()

    fun isImage() = pdfObject is PdfDictionary && pdfObject[PdfName.Subtype] == PdfName.Image

    fun isFont() = pdfObject is PdfDictionary && pdfObject[PdfName.Type] == PdfName.Font

    fun isCanParse() = isPageContents() || isImage() || isFont()

    override fun toString(): String {
        val name = pdfName?.toString() ?: ""
        val value = type.toString(pdfObject)
        if (name.isBlank()) {
            return value
        }
        if (value.isBlank()) {
            return name
        }
        return "$name : $value"
    }
}
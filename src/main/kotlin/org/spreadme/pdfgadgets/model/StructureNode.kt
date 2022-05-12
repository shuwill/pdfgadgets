package org.spreadme.pdfgadgets.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.itextpdf.kernel.pdf.*

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

    constructor(pdfName: PdfName? = null, pdfObject: PdfObject, level: Int = 0, paths: ArrayList<String>) : this(
        pdfName,
        pdfObject,
        getPdfObjectType(pdfObject.type.toInt()),
        level,
        paths
    ){
        if(pdfName != null) {
            paths.add(pdfName.toString())
        } else {
            paths.add(type.toString(pdfObject))
        }
    }

    fun hasChild(): Boolean = type.hasChild && pdfName != PdfName.Parent

    fun childs(): List<StructureNode> {
        if(childs.isNotEmpty()) {
            return childs
        }
        if (pdfObject is PdfDictionary && pdfName != PdfName.Parent) {
            childs = pdfObject.entrySet().map { StructureNode(it.key, it.value, level + 1, ArrayList(paths)) }.toList()
        } else if (pdfObject is PdfStream) {
            childs = pdfObject.entrySet().map { StructureNode(it.key, it.value, level + 1, ArrayList(paths)) }.toList()
        } else if (pdfObject is PdfArray) {
            childs = pdfObject.map { StructureNode(null, it, level + 1, ArrayList(paths)) }.toList()
        }
        return childs
    }

    fun isPdfStream() = pdfObject is PdfStream
    fun isSignatureContent() = pdfObject is PdfString && paths[paths.size - 2] == PdfName.V.toString()

    fun isParseable() = isPdfStream() || isSignatureContent()

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
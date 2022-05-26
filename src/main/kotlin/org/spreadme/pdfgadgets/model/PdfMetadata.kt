package org.spreadme.pdfgadgets.model

import com.itextpdf.kernel.pdf.PdfDictionary
import com.itextpdf.kernel.pdf.PdfDocument
import java.io.Closeable

class PdfMetadata(
    val fileMetadata: FileMetadata,
    val documentInfo: DocumentInfo,
    val numberOfPages: Int,
    val trailer: PdfDictionary,
    val structureRoot: StructureNode,
    var pages: List<PageMetadata> = listOf(),
    val document: PdfDocument,
    val outlines: Outlines = Outlines(document),
    var signatures: List<Signature> = listOf(),
) : Closeable {

    override fun close() {
        println("document close")
        document.close()
        pages.forEach { it.renderer.close() }
    }

}

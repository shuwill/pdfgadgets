package org.spreadme.pdfgadgets.model

import com.itextpdf.kernel.pdf.PdfDictionary
import com.itextpdf.kernel.pdf.PdfDocument

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
) : AutoCloseable {

    override fun close() {
        document.close()
    }

}

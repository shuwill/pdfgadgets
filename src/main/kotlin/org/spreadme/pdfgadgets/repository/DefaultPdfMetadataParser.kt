package org.spreadme.pdfgadgets.repository

import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.geom.Rectangle
import com.itextpdf.kernel.pdf.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.model.*

class DefaultPdfMetadataParser : PdfMetadataParser, KoinComponent {

    private val signatureParser by inject<SignatureParser>()

    override suspend fun parse(fileMetadata: FileMetadata): PdfMetadata {
        val document = PdfDocument(PdfReader(fileMetadata.file()))
        val numberOfPages = document.numberOfPages
        val documentInfo = DocumentInfo(document.pdfVersion.toString(), document.documentInfo)

        // get outlines
        val catalog = document.catalog
        val pdfOutline = document.getOutlines(true)
        val destNameTree = catalog.getNameTree(PdfName.Dests)
        val outlines = Outlines(
            document,
            if (pdfOutline == null) {
                mutableListOf()
            } else {
                pdfOutline.allChildren
            },
            destNameTree.names
        )

        // pdf renderer
        val renderer = DefaultPdfRenderer(fileMetadata)

        // signatures
        val signatures = signatureParser.parse(document)
        val lastSignatureCoversWholeDocument = signatures.isNotEmpty() &&
                signatures.last().signatureCoversWholeDocument
        val signatureMap = signatures.associateBy { it.fieldName }

        // pages info
        val pages = IntRange(1, numberOfPages).map {
            val originPage = document.getPage(it)
            val cropbox = cropbox(originPage)
            val mediabox = mediabox(originPage)
            val pageSize = cropbox ?: mediabox

            // get signatures from page
            val signaturesOfPage = originPage.annotations?.mapNotNull { a ->
                val merged: PdfDictionary = a.pdfObject
                val fieldName = merged.getAsString(PdfName.T)?.value ?: ""
                val signature = signatureMap[fieldName]
                if (signature != null) {
                    signature.lastSignatureCoversWholeDocument = lastSignatureCoversWholeDocument
                    val rectangle = a.rectangle?.toRectangle()
                    if (rectangle != null) {
                        signature.position = Position(it, pageSize, rectangle)
                    }
                }
                signature
            }?.toList() ?: listOf()

            PageMetadata(it, pageSize, mediabox, renderer, signaturesOfPage)

        }.toList()

        return PdfMetadata(
            fileMetadata,
            documentInfo,
            numberOfPages,
            document.trailer,
            StructureNode(document.trailer),
            pages,
            document,
            outlines,
            signatures
        )
    }

    private fun cropbox(page: PdfPage): Rectangle? {
        val cropBox = page.cropBox
        if (cropBox != null) {
            var pageSize = PageSize(cropBox)
            var rotation = page.rotation
            while (rotation > 0) {
                pageSize = pageSize.rotate()
                rotation -= 90
            }
            return pageSize
        }
        return null
    }

    private fun mediabox(page: PdfPage): Rectangle = page.pageSizeWithRotation
}
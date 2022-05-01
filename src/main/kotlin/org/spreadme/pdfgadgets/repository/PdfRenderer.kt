package org.spreadme.pdfgadgets.repository

import java.awt.image.BufferedImage

interface PdfRenderer: AutoCloseable {

    suspend fun render(index: Int, dpi: Float): BufferedImage
}
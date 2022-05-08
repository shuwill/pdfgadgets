package org.spreadme.pdfgadgets.repository

import java.awt.image.BufferedImage
import java.io.Closeable

interface PdfRenderer: Closeable {

    suspend fun render(index: Int, dpi: Float): BufferedImage
}
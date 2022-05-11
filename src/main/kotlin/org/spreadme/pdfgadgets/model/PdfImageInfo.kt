package org.spreadme.pdfgadgets.model

import java.awt.image.BufferedImage

data class PdfImageInfo(
    val bufferedImage: BufferedImage,
    val imageType: String?
)

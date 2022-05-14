package org.spreadme.pdfgadgets.model

import java.awt.image.BufferedImage

class PageRenderInfo(
    val pageImage: BufferedImage,
    val textRenderInfos: List<TextRenderInfo>
)

data class TextRenderInfo(
    val text: String,
    val position: Position
)
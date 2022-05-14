package org.spreadme.pdfgadgets.model

import java.awt.image.BufferedImage

class PageRenderInfo(
    val pageImage: BufferedImage,
    val textRenderInfos: List<TextRenderInfo>,
    val textBlockPositions: List<Position>
)

data class TextRenderInfo(
    val text: String,
    val position: Position
)
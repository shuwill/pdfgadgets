package org.spreadme.pdfgadgets.model

import java.awt.image.BufferedImage

class PageRenderInfo(
    val bufferedImage: BufferedImage,
    val textBlocks: List<TextBlock>,
)

data class TextBlock(
    val chars: List<Char>,
    val position: Position
) {
    override fun toString() = String(chars.toCharArray())
}
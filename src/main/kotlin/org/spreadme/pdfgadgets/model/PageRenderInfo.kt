package org.spreadme.pdfgadgets.model

class PageRenderInfo(
    val pixmapMetadata: PixmapMetadata,
    val textBlocks: List<TextBlock>,
)

data class TextBlock(
    val chars: List<Char>,
    val position: Position
) {
    override fun toString() = String(chars.toCharArray())
}
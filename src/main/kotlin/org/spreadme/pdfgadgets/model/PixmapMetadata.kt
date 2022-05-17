package org.spreadme.pdfgadgets.model

import java.awt.image.BufferedImage

data class PixmapMetadata(
    val width: Int,
    val height: Int,
    val pixels: IntArray
) {

    fun toBufferedImage(): BufferedImage {
        val image = BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB)
        image.setRGB(0, 0, width, height, pixels, 0, width)
        return image
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PixmapMetadata

        if (width != other.width) return false
        if (height != other.height) return false
        if (!pixels.contentEquals(other.pixels)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = width
        result = 31 * result + height
        result = 31 * result + pixels.contentHashCode()
        return result
    }
}

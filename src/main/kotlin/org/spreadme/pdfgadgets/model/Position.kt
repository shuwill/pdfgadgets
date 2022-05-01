package org.spreadme.pdfgadgets.model

import com.itextpdf.kernel.geom.Rectangle

data class Position(
    var index: Int,
    var pageSize: Rectangle,
    var rectangle: Rectangle,
) {

    override fun toString(): String {
        return "Position(index=$index, offsetX=$rectangle.x, offsetY=$rectangle.y)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Position

        if (index != other.index) return false
        if (!rectangle.equalsWithEpsilon(other.rectangle)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index
        result = 31 * result + rectangle.hashCode()
        return result
    }

}
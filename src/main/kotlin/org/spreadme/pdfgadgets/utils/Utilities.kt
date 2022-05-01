package org.spreadme.pdfgadgets.utils

import com.itextpdf.kernel.pdf.PdfDate
import java.util.*

inline fun <T> Boolean.choose(trueValue: T, falseValue: T): T {
    return if(this) {trueValue} else {falseValue}
}

fun String.pdfDate(): Date? {
    if (this.isBlank()) {
        return null
    }
    return PdfDate.decode(this).time
}

fun <E> ArrayList<E>.new(): ArrayList<E> {
    return ArrayList(this)
}
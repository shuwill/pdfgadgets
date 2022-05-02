package org.spreadme.pdfgadgets.utils

import com.itextpdf.kernel.pdf.PdfDate
import java.text.SimpleDateFormat
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

inline fun Date.format(format: String): String {
    val dateFormat = SimpleDateFormat(format)
    return dateFormat.format(this)
}

const val DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss"

inline fun Date.format(): String {
   return this.format(DEFAULT_FORMAT)
}

fun <E> ArrayList<E>.new(): ArrayList<E> {
    return ArrayList(this)
}
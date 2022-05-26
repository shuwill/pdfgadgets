package org.spreadme.pdfgadgets.utils

import com.itextpdf.kernel.pdf.PdfDate
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.ImagingOpException
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.ReadableByteChannel
import java.nio.channels.WritableByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

/**
 * generate uuid
 */
fun uuid() = UUID.randomUUID().toString()

/**
 * boolean chooser
 * @param trueValue
 * @param falseValue
 */
fun <T> Boolean.choose(trueValue: T, falseValue: T): T {
    return if (this) {
        trueValue
    } else {
        falseValue
    }
}

/**
 * decode pdfdata
 */
fun String.pdfDate(): Date? {
    if (this.isBlank()) {
        return null
    }
    return PdfDate.decode(this).time
}

/**
 * format date
 * @param format
 */
fun Date.format(format: String): String {
    val dateFormat = SimpleDateFormat(format)
    return dateFormat.format(this)
}

/**
 * defualt date format
 */
fun Date.format(): String {
    return this.format("yyyy-MM-dd HH:mm:ss")
}

fun PdfImageXObject.getBoolean(pdfName: PdfName, defaultValue: Boolean) =
    this.pdfObject?.getAsBoolean(pdfName)?.value ?: defaultValue

fun PdfImageXObject.getArray(pdfName: PdfName): FloatArray? =
    this.pdfObject?.getAsArray(pdfName)?.toFloatArray()

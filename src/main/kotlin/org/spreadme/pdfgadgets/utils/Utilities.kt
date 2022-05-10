package org.spreadme.pdfgadgets.utils

import com.itextpdf.kernel.pdf.PdfDate
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

/**
 * boolean chooser
 * @param trueValue
 * @param falseValue
 */
inline fun <T> Boolean.choose(trueValue: T, falseValue: T): T {
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
inline fun Date.format(format: String): String {
    val dateFormat = SimpleDateFormat(format)
    return dateFormat.format(this)
}

/**
 * defualt date format
 */
inline fun Date.format(): String {
    return this.format("yyyy-MM-dd HH:mm:ss")
}

/**
 * copy inputsteam to outputstream
 *
 * @param input inputsteam
 * @param output outputstream
 * @throws IOException IOException
 */
@Throws(IOException::class)
fun copy(input: InputStream, output: OutputStream) {
    val readableChannel = Channels.newChannel(input)
    val writableChannl = Channels.newChannel(output)
    copy(readableChannel, writableChannl)
}

/**
 * copy ReadableByteChannel to WritableByteChannel
 *
 * @param readableChan ReadableByteChannel
 * @param writableChan WritableByteChannel
 * @throws IOException IOException
 */
@Throws(IOException::class)
fun copy(readableChan: ReadableByteChannel, writableChan: WritableByteChannel) {
    val byteBuffer = ByteBuffer.allocate(8092)
    while (readableChan.read(byteBuffer) != -1) {
        byteBuffer.flip()
        writableChan.write(byteBuffer)
        byteBuffer.compact()
    }
    byteBuffer.flip()
    while (byteBuffer.hasRemaining()) {
        writableChan.write(byteBuffer)
    }
}

/**
 * create file or directories
 *
 * @param path path
 * @param isFile is file?
 * @return File
 * @throws IOException IOException
 */
@Throws(IOException::class)
fun createFile(path: Path, isFile: Boolean): File? {
    val filePath = path.normalize()
    if (!Files.exists(filePath)) {
        if (isFile) {
            val dirPath = filePath.parent
            Files.createDirectories(dirPath)
            Files.createFile(filePath)
            return filePath.toFile()
        }
        Files.createDirectories(filePath)
    }
    return filePath.toFile()
}
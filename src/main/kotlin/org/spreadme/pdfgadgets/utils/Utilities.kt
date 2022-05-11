package org.spreadme.pdfgadgets.utils

import com.itextpdf.kernel.pdf.PdfDate
import com.itextpdf.kernel.pdf.PdfName
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject
import java.awt.RenderingHints
import java.awt.geom.AffineTransform
import java.awt.image.*
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

/**
 * @param image The image to apply the mask to as alpha channel.
 * @param mask A mask image in 8 bit Gray. Even for a stencil mask image due to
 * [.getOpaqueImage]'s `from1Bit()` special
 * handling of DeviceGray.
 * @param interpolateMask interpolation flag of the mask image.
 * @param isSoft `true` if a soft mask. If not stencil mask, then alpha will be inverted
 * by this method.
 * @param matte an optional RGB matte if a soft mask.
 * @return an ARGB image (can be the altered original image)
 */
fun applyMask(image: BufferedImage, mask: BufferedImage?, interpolateMask: Boolean, isSoft: Boolean, matte: FloatArray?): BufferedImage {
    var bufferedImage = image
    var maskImage = mask ?: return bufferedImage
    val width = bufferedImage.width.coerceAtLeast(maskImage.width)
    val height = bufferedImage.height.coerceAtLeast(maskImage.height)

    // scale mask to fit image, or image to fit mask, whichever is larger.
    // also make sure that mask is 8 bit gray and image is ARGB as this
    // is what needs to be returned.
    if (maskImage.width < width || maskImage.height < height) {
        maskImage = scaleImage(maskImage, width, height, BufferedImage.TYPE_BYTE_GRAY, interpolateMask)
    } else if (maskImage.type != BufferedImage.TYPE_BYTE_GRAY) {
        maskImage = scaleImage(maskImage, width, height, BufferedImage.TYPE_BYTE_GRAY, false)
    }
    if (bufferedImage.width < width || bufferedImage.height < height) {
        bufferedImage = scaleImage(bufferedImage, width, height, BufferedImage.TYPE_INT_ARGB, interpolateMask)
    } else if (bufferedImage.type != BufferedImage.TYPE_INT_ARGB) {
        bufferedImage = scaleImage(bufferedImage, width, height, BufferedImage.TYPE_INT_ARGB, false)
    }

    // compose alpha into ARGB image, either:
    // - very fast by direct bit combination if not a soft mask and a 8 bit alpha source.
    // - fast by letting the sample model do a bulk band operation if no matte is set.
    // - slow and complex by matte calculations on individual pixel components.
    val raster = bufferedImage.raster
    val alpha = maskImage.raster
    if (!isSoft && raster.dataBuffer.size == alpha.dataBuffer.size) {
        val dst = raster.dataBuffer
        val src = alpha.dataBuffer
        var i = 0
        var c = dst.size
        while (c > 0) {
            dst.setElem(i, dst.getElem(i) and 0xffffff or (src.getElem(i).inv() shl 24))
            i++
            c--
        }
    } else if (matte == null) {
        val samples = IntArray(width)
        for (y in 0 until height) {
            alpha.getSamples(0, y, width, 1, 0, samples)
            if (!isSoft) {
                for (x in 0 until width) {
                    samples[x] = samples[x] xor -1
                }
            }
            raster.setSamples(0, y, width, 1, 3, samples)
        }
    } else {
        val alphas = IntArray(width)
        val pixels = IntArray(4 * width)
        // Original code is to clamp component and alpha to [0f, 1f] as matte is,
        // and later expand to [0; 255] again (with rounding).
        // component = 255f * ((component / 255f - matte) / (alpha / 255f) + matte)
        //           = (255 * component - 255 * 255f * matte) / alpha + 255f * matte
        // There is a clearly visible factor 255 for most components in above formula,
        // i.e. max value is 255 * 255: 16 bits + sign.
        // Let's use faster fixed point integer arithmetics with Q16.15,
        // introducing neglible errors (0.001%).
        // Note: For "correct" rounding we increase the final matte value (m0h, m1h, m2h) by
        // a half an integer.
        val fraction = 15
        val factor = 255 shl fraction
        val m0 = (factor * matte[0]).roundToInt() * 255
        val m1 = (factor * matte[1]).roundToInt() * 255
        val m2 = (factor * matte[2]).roundToInt() * 255
        val m0h = m0 / 255 + (1 shl fraction - 1)
        val m1h = m1 / 255 + (1 shl fraction - 1)
        val m2h = m2 / 255 + (1 shl fraction - 1)
        for (y in 0 until height) {
            raster.getPixels(0, y, width, 1, pixels)
            alpha.getSamples(0, y, width, 1, 0, alphas)
            var offset = 0
            for (x in 0 until width) {
                val a = alphas[x]
                if (a == 0) {
                    offset += 3
                } else {
                    pixels[offset] = clampColor((pixels[offset++] * factor - m0) / a + m0h shr fraction)
                    pixels[offset] = clampColor((pixels[offset++] * factor - m1) / a + m1h shr fraction)
                    pixels[offset] = clampColor((pixels[offset++] * factor - m2) / a + m2h shr fraction)
                }
                pixels[offset++] = a
            }
            raster.setPixels(0, y, width, 1, pixels)
        }
    }
    return bufferedImage
}

private fun clampColor(color: Int): Int {
    return if (color < 0) 0 else color.coerceAtMost(255)
}

/**
 * High-quality image scaling.
 */
private fun scaleImage(image: BufferedImage, width: Int, height: Int, type: Int, interpolate: Boolean): BufferedImage {
    var inter = interpolate
    val imgWidth = image.width
    val imgHeight = image.height
    // largeScale switch is arbitrarily chosen as to where bicubic becomes very slow
    val largeScale = width * height > 3000 * 3000 * if (type == BufferedImage.TYPE_BYTE_GRAY) 3 else 1
    inter = inter and (imgWidth != width || imgHeight != height)
    val image2 = BufferedImage(width, height, type)
    if (inter) {
        val af = AffineTransform.getScaleInstance(width.toDouble() / imgWidth, height.toDouble() / imgHeight)
        val afo = AffineTransformOp(af, if (largeScale) AffineTransformOp.TYPE_BILINEAR else AffineTransformOp.TYPE_BICUBIC)
        try {
            afo.filter(image, image2)
            return image2
        } catch (e: ImagingOpException) {
            e.printStackTrace()
        }
    }
    val g = image2.createGraphics()
    if (inter) {
        g.setRenderingHint(
            RenderingHints.KEY_INTERPOLATION,
            if (largeScale) RenderingHints.VALUE_INTERPOLATION_BILINEAR else RenderingHints.VALUE_INTERPOLATION_BICUBIC
        )
        g.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            if (largeScale) RenderingHints.VALUE_RENDER_DEFAULT else RenderingHints.VALUE_RENDER_QUALITY
        )
    }
    g.drawImage(image, 0, 0, width, height, 0, 0, imgWidth, imgHeight, null)
    g.dispose()
    return image2
}

fun PdfImageXObject.getBoolean(pdfName: PdfName, defaultValue: Boolean) =
    this.pdfObject?.getAsBoolean(pdfName)?.value ?: defaultValue

fun PdfImageXObject.getArray(pdfName: PdfName): FloatArray? =
    this.pdfObject?.getAsArray(pdfName)?.toFloatArray()

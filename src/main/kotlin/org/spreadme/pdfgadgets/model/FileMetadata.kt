package org.spreadme.pdfgadgets.model

import java.io.File
import java.nio.file.Paths
import java.util.*

data class FileMetadata(
    val path: String,
    val name: String,
    val thumbnail: String? = null,
    val length: Long,
    val openTime: Date
) {

    fun file(): File = Paths.get(path).toFile()
}

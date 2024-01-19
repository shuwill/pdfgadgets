package org.spreadme.pdfgadgets.config

import com.artifex.mupdf.fitz.Context
import mu.KotlinLogging
import org.spreadme.common.OS
import org.spreadme.common.Platform
import java.nio.file.Files
import java.nio.file.Paths

object MupdfConfig : Context.MupdfLibLoader{

    private val logger = KotlinLogging.logger {}
    private const val MUPDF_LIBRARY_PATH_PROPERTY = "mupdf.library.path"

    var cache_url = "https://spreadme.oss-cn-shanghai.aliyuncs.com/mupdf"
    val cache_root = "${AppConfig.appPath}/.mupdf/"
    val libName = "mupdf-${Platform.os.id}-${Platform.arch.id}.${Platform.os.ext}"

    override fun load(): String {
        val libPath = System.getProperty(MUPDF_LIBRARY_PATH_PROPERTY)
        if(libPath != null) {
            val library = Paths.get(libPath, libName)
            if(Files.exists(library)) {
                logger.info("find the mupdf lib on $library")
                return library.toString()
            }
        }
        val library = Paths.get(cache_root, libName)
        if(Files.exists(library)) {
            logger.info("find the mupdf lib on $library")
            return library.toString()
        }

        return "${System.getProperty("java.home")}/${if (Platform.os == OS.Windows) "bin" else "natives"}/$libName"
    }
}
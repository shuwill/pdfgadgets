package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.Context
import mu.KotlinLogging
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.utils.OS
import org.spreadme.pdfgadgets.utils.Platform
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

object MupdfLibLoader : Context.MupdfLibLoader {

    private val logger = KotlinLogging.logger {}

    private const val MUPDF_LIBRARY_PATH_PROPERTY = "mupdf.library.path"
    private val CACHE_ROOT = "${AppConfig.appPath}/.mupdf/"

    private val platform: Platform = Platform()

    @Synchronized
    override fun load(): String {
        val libName = "mupdf-${platform.os.id}-${platform.arch.id}.${platform.os.ext}"
        logger.info("load the mupdf lib $libName")
        val libPath = System.getProperty(MUPDF_LIBRARY_PATH_PROPERTY)
        if(libPath != null) {
            val library = Paths.get(libPath, libName)
            if(Files.exists(library)) {
                logger.info("find the mupdf lib on $library")
                return library.toString()
            }
        }
        val library = Paths.get(CACHE_ROOT, libName)
        if(Files.exists(library)) {
            logger.info("find the mupdf lib on $library")
            return library.toString()
        } else {
            val cacheRootPath = Paths.get(CACHE_ROOT)
            if (!Files.exists(cacheRootPath)) {
                Files.createDirectory(cacheRootPath)
            }
            MupdfLibLoader::class.java.getResourceAsStream("/natives/$libName")?.use { input ->
                Files.copy(input, library, StandardCopyOption.REPLACE_EXISTING)
                logger.info("find the mupdf lib on $library")
                return library.toString()
            }
        }

        return "${System.getProperty("java.home")}/${if (platform.os == OS.Windows) "bin" else "natives"}/$libName"
    }
}
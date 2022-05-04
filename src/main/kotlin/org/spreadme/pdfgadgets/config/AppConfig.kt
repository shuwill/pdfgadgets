package org.spreadme.pdfgadgets.config

import org.spreadme.pdfgadgets.common.DBHelper
import org.spreadme.pdfgadgets.model.FileMetadatas
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

object AppConfig {

    val appName: String = "PDFGadgets"
    val appPath: Path = Paths.get(System.getProperty("user.home"), ".pdfgadgets")
    val indexPath: Path = Paths.get(appPath.toString(), "index")

    init {
        // create config floder
        if (!Files.exists(appPath)) {
            Files.createDirectories(appPath)
        }
        // create index floder
        if (!Files.exists(indexPath)) {
            Files.createDirectory(indexPath)
        }

        DBHelper.help("$appPath/$appName.db").createTable(FileMetadatas)
    }

    fun appIcon(resourceId: String): BufferedImage {
        // Retrieving image
        val resourceFile = AppConfig::class.java.classLoader.getResourceAsStream(resourceId)
        val imageInput = ImageIO.read(resourceFile)

        val newImage = BufferedImage(
            imageInput.width,
            imageInput.height,
            BufferedImage.TYPE_INT_ARGB
        )

        // Drawing
        val canvas = newImage.createGraphics()
        canvas.drawImage(imageInput, 0, 0, null)
        canvas.dispose()

        return newImage
    }
}
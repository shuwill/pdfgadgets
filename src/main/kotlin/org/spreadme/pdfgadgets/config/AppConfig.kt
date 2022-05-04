package org.spreadme.pdfgadgets.config

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.jetbrains.exposed.sql.Table
import org.spreadme.pdfgadgets.model.FileMetadatas
import org.spreadme.pdfgadgets.model.FileMetadatas.autoIncrement
import java.awt.image.BufferedImage
import java.nio.file.Path
import java.nio.file.Paths
import javax.imageio.ImageIO

object AppConfig {

    val appName: String = "PDFGadgets"
    val appPath: Path = Paths.get(System.getProperty("user.home"), ".pdfgadgets")
    val indexPath: Path = Paths.get(appPath.toString(), "index")

    var isDark: MutableState<Boolean> = mutableStateOf(false)

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

object AppConfigs: Table("APP_CONFIGS") {

    val DARK_CONFIG = "isDark"

    val id = integer("id").autoIncrement()
    val key = varchar("configKey", 500)
    val value = text("configVey")

    override val primaryKey = PrimaryKey(AppConfigs.id, name = "PK_APP_CONFIGS_ID")

}
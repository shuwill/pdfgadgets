package org.spreadme.pdfgadgets.repository

import com.artifex.mupdf.fitz.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.config.AppConfigs
import org.spreadme.pdfgadgets.config.DBHelper
import org.spreadme.pdfgadgets.config.MupdfConfig
import org.spreadme.pdfgadgets.model.FileMetadatas
import org.spreadme.pdfgadgets.utils.copy
import org.spreadme.pdfgadgets.utils.createFile
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths

class DefaultAppConfigRepository : AppConfigRepository {

    private val logger = KotlinLogging.logger {}

    override suspend fun load(message: MutableStateFlow<String>) {
        // create config floder
        if (!Files.exists(AppConfig.appPath)) {
            withContext(Dispatchers.IO) {
                Files.createDirectories(AppConfig.appPath)
            }
        }

        // download the mupdf lib
        message.value = "load the mupdf lib"
        logger.info("load the mupdf lib ${MupdfConfig.libName}")
        val library = Paths.get(MupdfConfig.cache_root, MupdfConfig.libName)
        if (!Files.exists(library)) {
            val mupdfLibUrl = "${MupdfConfig.cache_url}/${MupdfConfig.libName}"

            message.value = "dowload the mupdf lib"

            logger.info("dowload the mupdf lib from $mupdfLibUrl")
            withContext(Dispatchers.IO) {
                URL(mupdfLibUrl).openStream().use { input ->
                    createFile(library, true)
                    Files.newOutputStream(library).buffered().use { output ->
                        copy(input, output)
                    }
                }
            }
        }
        // load the mupdf lib
        Context.setLibLoader(MupdfConfig)
        Context.init()

        // open the db
        message.value = "open the app db"
        DBHelper.help("${AppConfig.appPath}/${AppConfig.appName}.db")
            .createTable(FileMetadatas)
            .createTable(AppConfigs)
            .upgrade()

        // load some config from db
        message.value = "load application config"
        transaction {
            AppConfigs.selectAll().forEach {
                if (it[AppConfigs.key] == AppConfigs.DARK_CONFIG) {
                    AppConfig.isDark.value = it[AppConfigs.value].toBoolean()
                }
            }
        }
    }

    override suspend fun config(configKey: String, configValue: String) {
        transaction {
            val ids = AppConfigs.select { (AppConfigs.key.eq(configKey)) }
                .map { it[AppConfigs.id] }
                .toList()
            if (ids.isNotEmpty()) {
                AppConfigs.update({ AppConfigs.id.eq(ids.first()) }) {
                    it[value] = configValue
                }
            } else {
                AppConfigs.insert {
                    it[key] = configKey
                    it[value] = configValue
                }
            }
        }
    }

    override suspend fun getConfig(configKey: String): String {
        return transaction {
            val values = AppConfigs.select { AppConfigs.key.eq(configKey) }
                .map { it[AppConfigs.value] }
                .toList()
            if (values.isNotEmpty()) {
                values.first()
            } else {
                ""
            }
        }
    }
}
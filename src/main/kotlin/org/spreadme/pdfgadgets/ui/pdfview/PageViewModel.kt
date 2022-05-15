package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.mutableStateListOf
import mu.KotlinLogging
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.PageMetadata
import org.spreadme.pdfgadgets.model.Position
import java.nio.file.Files

class PageViewModel(
    val page: PageMetadata
): ViewModel() {

    private val logger = KotlinLogging.logger {}

    val searchPosition = mutableStateListOf<Position>()

    override fun onCleared() {
        val pageImagePath = page.pageImagePath()
        pageImagePath?.let {
            if (Files.exists(it)) {
                Files.delete(it)
                logger.debug("delete the index $pageImagePath")
            }
        }
    }
}


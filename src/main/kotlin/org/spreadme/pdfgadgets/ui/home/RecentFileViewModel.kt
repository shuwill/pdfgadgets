package org.spreadme.pdfgadgets.ui.home

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.*
import org.spreadme.pdfgadgets.common.AbstractViewModel
import org.spreadme.pdfgadgets.model.FileMetadata
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import kotlin.coroutines.CoroutineContext

class RecentFileViewModel(
    private val fileMetadataRepository: FileMetadataRepository
) : AbstractViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()

    val fileMetadatas = mutableStateListOf<FileMetadata>()

    init {
        launch {
            fileMetadatas.addAll(fileMetadataRepository.query())
        }
    }

    override fun clear() {
        coroutineContext.cancel()
    }
}
package org.spreadme.pdfgadgets.ui.home

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.launch
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.common.viewModelScope
import org.spreadme.pdfgadgets.model.FileMetadata
import org.spreadme.pdfgadgets.repository.FileMetadataRepository

class RecentFileViewModel(
    val fileMetadataRepository: FileMetadataRepository
) : ViewModel() {

    val fileMetadatas = mutableStateListOf<FileMetadata>()

    init {
        viewModelScope.launch {
            fileMetadatas.addAll(fileMetadataRepository.query())
        }
    }

    fun reacquire() {
        fileMetadatas.clear()
        viewModelScope.launch {
            fileMetadatas.addAll(fileMetadataRepository.query())
        }
    }

}
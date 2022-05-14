package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.runtime.mutableStateListOf
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.model.PageMetadata
import org.spreadme.pdfgadgets.model.Position

class PageViewModel(
    val page: PageMetadata
): ViewModel() {

    val searchPosition = mutableStateListOf<Position>()
}
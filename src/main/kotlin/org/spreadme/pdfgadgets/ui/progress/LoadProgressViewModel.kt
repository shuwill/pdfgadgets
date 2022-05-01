package org.spreadme.pdfgadgets.ui.progress

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.ViewModel

class LoadProgressViewModel : ViewModel {

    var loading by mutableStateOf(true)
    var successful by mutableStateOf(false)
    var message by mutableStateOf<String?>(null)

    var onSuccess: () -> Unit = {}

    fun success() {
        loading = false
        successful = true
    }

    fun fail(errorMessage: String?) {
        loading = false
        successful = false
        message = errorMessage
    }
}
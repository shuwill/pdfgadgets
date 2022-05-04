package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.AbstractViewModel

class LoadProgressViewModel : AbstractViewModel() {

    var status by mutableStateOf(LoadProgressStatus.NONE)
    var message by mutableStateOf("")
    var onSuccess: () -> Unit = {}

    fun start() {
        status = LoadProgressStatus.LOADING
    }

    fun success() {
        status = LoadProgressStatus.SUCCESSFUL
    }

    fun fail(message: String) {
        status = LoadProgressStatus.FAILURE
        this.message = message
    }

}

enum class LoadProgressStatus {
    NONE,
    LOADING,
    SUCCESSFUL,
    FAILURE
}
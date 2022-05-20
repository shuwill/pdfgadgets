package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.spreadme.pdfgadgets.common.ViewModel

class LoadProgressViewModel : ViewModel() {

    var status by mutableStateOf(LoadProgressStatus.NONE)
    var message by mutableStateOf("")
    var onSuccess: () -> Unit = {}
    var onFail: () -> Unit = {}

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

    fun needPassword() {
        status = LoadProgressStatus.NEED_PASSWORD
    }

}

enum class LoadProgressStatus {
    NONE,
    LOADING,
    SUCCESSFUL,
    NEED_PASSWORD,
    FAILURE
}
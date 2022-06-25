package org.spreadme.pdfgadgets.ui.tool

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.spreadme.pdfgadgets.ui.common.Dialog

@Composable
fun DocumentSignatureDialog(
    enabled: MutableState<Boolean> = mutableStateOf(false)
) {
    if (enabled.value) {
        Dialog(
            onClose = {
                enabled.value = false;
            },
            title = "签名文档"
        ) {

        }
    }
}
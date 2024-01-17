package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors

@Composable
fun MainApplicationFrame(
    applicationViewModel: ApplicationViewModel,
    content: @Composable BoxScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxSize().clickable { focusManager.clearFocus() }) {
                    Column(Modifier.fillMaxSize()) {
                        Divider(color = LocalExtraColors.current.border)
                        Box(Modifier.fillMaxSize()) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
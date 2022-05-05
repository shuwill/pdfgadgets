package org.spreadme.pdfgadgets.ui.common

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Toast(
    modifier: Modifier = Modifier,
    enabledState: MutableState<Boolean> = mutableStateOf(true),
    timeout: Long = 5000,
    onFinished: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    var enabled by remember { enabledState }
    AnimatedVisibility(
        enabled,
        enter = fadeIn() + expandIn(),
        exit = shrinkOut() + fadeOut()
    ) {
        Row(
            Modifier.wrapContentSize().then(modifier),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }

    MainScope().launch {
        delay(timeout)
        enabled = false
        onFinished()
    }

}
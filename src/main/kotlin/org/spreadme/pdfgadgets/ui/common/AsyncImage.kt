package org.spreadme.pdfgadgets.ui.common

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    onFailure: (Exception) -> Unit = {},
    painterFor: @Composable (T) -> Painter,
    contentDescription: String = "",
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {

    val image: T? by produceState<T?>(null) {
        try {
            value = load()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier
        )
    }
}
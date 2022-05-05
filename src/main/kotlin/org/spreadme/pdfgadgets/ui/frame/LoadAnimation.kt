package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle

@Composable
fun LoadAnimation(modifier: Modifier = Modifier) {

    var rotation by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = Unit, block = {
        while (true) {
            animate(
                0f,
                90f,
                animationSpec = tween(250, easing = LinearEasing),
                block = { value, _ -> rotation = value }
            )
            animate(
                48f, 0f,
                animationSpec = tween(500, easing = LinearEasing),
                block = { value, _ -> height = value }
            )
        }
    })

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.wrapContentSize()) {
            val topLeft = Offset(this.center.x - 32f, this.center.y - 32f)
            rotate(degrees = rotation) {
                drawRect(
                    Color.White, topLeft, size = Size(64f, 64f),
                    style = Stroke(width = 8f)
                )
            }

            drawRect(
                Color.White, topLeft, size = Size(64f, height)
            )
        }
    }
}

@Composable
fun LoadText(
    message: String,
    color: Color = Color.Unspecified,
    style: TextStyle = LocalTextStyle.current,
    modifier: Modifier = Modifier
) {
    var enabled by remember { mutableStateOf(true) }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (enabled) {
            1f
        } else {
            0.2f
        },
        animationSpec = tween(200),
        finishedListener = {
            enabled = !enabled
        }
    )

    Text(
        text = message,
        color = color,
        style = style,
        modifier = modifier.alpha(animatedAlpha)
    )

    LaunchedEffect(Unit) {
        enabled = !enabled
    }
}
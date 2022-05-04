package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.animation.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.repository.AppConfigRepository
import org.spreadme.pdfgadgets.resources.R
import kotlin.coroutines.CoroutineContext

class AppLoadIndicator : KoinComponent, CoroutineScope, AutoCloseable {

    private val appConfigRepository by inject<AppConfigRepository>()
    private val applicationViewModel by inject<ApplicationViewModel>()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun indicate(
        finishedState: MutableState<Boolean>,
        content: @Composable () -> Unit
    ) {
        var finished by remember { finishedState }

        if (!finished) {
            val message = MutableStateFlow("")
            AppLoadProgressIndicator(message)
            launch {
                appConfigRepository.load(message)
                applicationViewModel.newBlankTab()
                finished = true
            }
        }
        AnimatedVisibility(
            finished,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
        ) {
            content()
        }
    }

    override fun close() {
        coroutineContext.cancel()
    }
}

@Composable
fun AppLoadProgressIndicator(message: MutableStateFlow<String>) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(Modifier.fillMaxSize().zIndex(-1f)) {
            Image(
                painter = painterResource(R.Drawables.indicate),
                contentDescription = "",
                contentScale = ContentScale.Inside,
                modifier = Modifier.fillMaxSize()
            )
        }
        LoadAnimation(Modifier.fillMaxSize().zIndex(1f))
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colors.primary.copy(0.95f)),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.padding(bottom = 32.dp).height(32.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val text by message.collectAsState()
                LoadText(
                    text,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }

    }
}

@Composable
@Preview
fun AppLoadProgressIndicatorPreview() {
    AppLoadProgressIndicator(MutableStateFlow("dowload the mupdf lib from https://spreadme.oss-cn-shanghai.aliyuncs.com/mupdf/mupdf-macos-arm64.dylib"))
}

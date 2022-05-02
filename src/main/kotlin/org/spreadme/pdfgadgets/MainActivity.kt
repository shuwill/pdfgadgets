package org.spreadme.pdfgadgets

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.spreadme.pdfgadgets.common.Activity
import org.spreadme.pdfgadgets.common.ActivityIntent
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.PlatformUI
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrame
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrameViewModel
import org.spreadme.pdfgadgets.ui.home.HomeComponent
import org.spreadme.pdfgadgets.ui.theme.PDFGadgetsTheme
import java.awt.Taskbar

class MainActivity : Activity() {

    companion object {
        fun getStartIntent(): ActivityIntent {
            return ActivityIntent(MainActivity::class).apply {

            }
        }
    }

    override fun onCreate() {
        if (Taskbar.isTaskbarSupported()) {
            Taskbar.getTaskbar().iconImage = AppConfig.appIcon(R.Drawables.appIcon)
        }

        val frameViewModel = ApplicationFrameViewModel()
        frameViewModel.newBlankTab()

        application {
            val windowState = rememberWindowState(width = 1224.dp, height = 800.dp)
            Window(
                onCloseRequest = {
                    onDestory()
                    exitApplication()
                },
                icon = painterResource(R.Drawables.appIcon),
                title = AppConfig.appName,
                state = windowState
            ) {
                // listening the state of the window
                LaunchedEffect(windowState) {
                    snapshotFlow { windowState.size }
                        .onEach { frameViewModel.onWindowStateChange(it) }
                        .launchIn(this)
                }

                PDFGadgetsTheme(frameViewModel.isDark) {

                    frameViewModel.composeWindow = window
                    frameViewModel.windowState = windowState

                    // custom the window title
                    val platformUI = PlatformUI(window.rootPane, frameViewModel.isDark)
                    if (platformUI.isSupportCustomWindowDecoration()) {
                        platformUI.customWindowDecoration()
                        frameViewModel.customWindowDecoration(true)
                    }

                    val frameViewModelState = remember { frameViewModel }
                    ApplicationFrame(frameViewModelState)
                }
            }
        }
    }

}
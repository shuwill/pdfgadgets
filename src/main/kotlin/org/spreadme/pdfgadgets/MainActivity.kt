package org.spreadme.pdfgadgets

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.Activity
import org.spreadme.pdfgadgets.common.ActivityIntent
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.PlatformUI
import org.spreadme.pdfgadgets.ui.frame.AppLoadIndicator
import org.spreadme.pdfgadgets.ui.frame.ApplicationFrame
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel
import org.spreadme.pdfgadgets.ui.theme.PDFGadgetsTheme
import java.awt.Taskbar

class MainActivity : Activity(), KoinComponent {

    companion object {
        fun getStartIntent(): ActivityIntent {
            return ActivityIntent(MainActivity::class).apply {

            }
        }
    }

    private val appLoadFinished = mutableStateOf(false)
    private val appLoadIndicator = AppLoadIndicator()

    private val applicationViewModel by inject<ApplicationViewModel>()

    override fun onCreate() {
        if (Taskbar.isTaskbarSupported()) {
            Taskbar.getTaskbar().iconImage = AppConfig.appIcon(R.Drawables.appIcon)
        }

        application {
            val windowState = rememberWindowState(width = 1278.dp, height = 760.dp)
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
                        .onEach { applicationViewModel.onWindowStateChange(it) }
                        .launchIn(this)
                }

                PDFGadgetsTheme(applicationViewModel.isDark) {
                    // custom the window title
                    val platformUI = PlatformUI(window.rootPane, applicationViewModel.isDark)
                    if (platformUI.isSupportCustomWindowDecoration()) {
                        platformUI.customWindowDecoration()
                        applicationViewModel.customWindowDecoration(true)
                    }

                    appLoadIndicator.indicate(appLoadFinished) {
                        applicationViewModel.composeWindow = window
                        applicationViewModel.windowState = windowState

                        ApplicationFrame(applicationViewModel)
                    }
                }
            }
        }
    }

    override fun onDestory() {
        appLoadIndicator.close()
        applicationViewModel.clear()
    }
}
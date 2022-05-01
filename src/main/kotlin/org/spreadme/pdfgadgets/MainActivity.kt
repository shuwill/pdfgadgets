package org.spreadme.pdfgadgets

import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.spreadme.pdfgadgets.common.Activity
import org.spreadme.pdfgadgets.common.ActivityIntent
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.resources.R
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
        frameViewModel.addComponent(HomeComponent(frameViewModel))

        application {
            val windowState = rememberWindowState(width = 1224.dp, height = 800.dp)
            frameViewModel.windowState = windowState
            Window(
                onCloseRequest = {
                    onDestory()
                    exitApplication()
                },
                icon = painterResource(R.Drawables.appIcon),
                title = AppConfig.appName,
                state = windowState
            ) {
                PDFGadgetsTheme(frameViewModel.isDark) {
                    val frameViewModelState = remember { frameViewModel }
                    ApplicationFrame(window, frameViewModelState)
                }
            }
        }
    }

}
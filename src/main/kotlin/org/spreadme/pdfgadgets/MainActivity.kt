package org.spreadme.pdfgadgets

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.TitleBarStyle
import org.spreadme.pdfgadgets.common.Activity
import org.spreadme.pdfgadgets.common.ActivityIntent
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.frame.AppFrameComponent
import org.spreadme.pdfgadgets.ui.frame.ApplicationBootstrap
import org.spreadme.pdfgadgets.ui.frame.TitleBarView
import org.spreadme.pdfgadgets.ui.theme.IntUiThemes
import org.spreadme.pdfgadgets.ui.theme.PDFGadgetsTheme
import java.awt.Taskbar
import java.awt.Toolkit

class MainActivity : Activity() {

    companion object {
        fun getStartIntent(): ActivityIntent {
            return ActivityIntent(MainActivity::class).apply {
                System.setProperty("pdfgadgets.logdir", AppConfig.appPath.toString())
            }
        }
    }

    private val appFrameComponent = AppFrameComponent()
    private val appFrameViewModel = appFrameComponent.viewModel()
    private val applicationBootstrap = ApplicationBootstrap(appFrameViewModel)

    @OptIn(ExperimentalTextApi::class)
    override fun onCreate() {
        if (Taskbar.isTaskbarSupported()) {
            val taskbar = Taskbar.getTaskbar()
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                Taskbar.getTaskbar().iconImage = AppConfig.appIcon(R.Drawables.appIcon)
            }
        }

        val screen = Toolkit.getDefaultToolkit().screenSize

        application {
            val windowState = rememberWindowState(width = (screen.width * .80).dp, height = (screen.height * .80).dp)
            val textStyle = TextStyle(fontFamily = FontFamily("Inter"))
            val themeDefinition =
                if (appFrameViewModel.theme.isDark()) {
                    JewelTheme.darkThemeDefinition(defaultTextStyle = textStyle)
                } else {
                    JewelTheme.lightThemeDefinition(defaultTextStyle = textStyle)
                }


            IntUiTheme(
                themeDefinition,
                ComponentStyling.decoratedWindow(
                    titleBarStyle = when (appFrameViewModel.theme) {
                        IntUiThemes.Light -> TitleBarStyle.lightWithLightHeader()
                        IntUiThemes.Dark -> TitleBarStyle.dark()
                        IntUiThemes.System -> if (appFrameViewModel.theme.isDark()) {
                            TitleBarStyle.dark()
                        } else {
                            TitleBarStyle.light()
                        }
                    },
                ),
            ) {
                DecoratedWindow(
                    onCloseRequest = {
                        onDestory()
                        exitApplication()
                    }, icon = painterResource(R.Drawables.appIcon), title = AppConfig.appName, state = windowState
                ) {
                    // listening the state of the window
                    LaunchedEffect(windowState) {
                        snapshotFlow { windowState.size }
                            .onEach {
                                appFrameViewModel.onWindowStateChange(it)
                            }.launchIn(this)
                    }

                    TitleBarView(appFrameViewModel)

                    // load the window state
                    appFrameViewModel.composeWindow = window
                    appFrameViewModel.windowState = windowState

                    PDFGadgetsTheme(appFrameViewModel.isDark) {
                        applicationBootstrap.bootstrap {
                            appFrameComponent.onRender()
                        }
                    }
                }
            }
        }
    }

    override fun onDestory() {
        appFrameComponent.close()
    }
}
package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.*
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.common.LoadableComponent
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.ui.home.HomeComponent
import org.spreadme.pdfgadgets.ui.progress.LoadProgressComponent
import org.spreadme.pdfgadgets.ui.progress.LoadProgressViewModel
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class ApplicationFrameViewModel : ViewModel, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()


    var composeWindow = ComposeWindow()

    val components = mutableStateListOf<AbstractComponent>()
    var currentComponent by mutableStateOf<AbstractComponent?>(null)

    //UI State
    var windowState = WindowState()
    var isDark by mutableStateOf(false)
    var tabWidth by mutableStateOf(168)

    var isCustomWindowDecoration = false
    var tabbarPaddingStart = 16
    var tabbarPaddingEnd = 16
    val iconSize = 32

    fun customWindowDecoration(enabled: Boolean) {
        isCustomWindowDecoration = enabled
        tabbarPaddingStart = 80
    }

    fun onWindowStateChange(size: DpSize) {
        println("onWindowResize $size")
        calculateWidth()
    }

    fun onSelectTab(selectedComponent: AbstractComponent) {
        currentComponent = selectedComponent
    }

    fun onClose(closeComponent: AbstractComponent) {
        closeComponent.close()
        val index = components.indexOf(closeComponent)
        currentComponent = if (index == 0 && components.size == 1) {
            components.remove(closeComponent)
            exitProcess(0)
        } else if (index == 0) {
            components[1]
        } else {
            components[index - 1]
        }
        components.remove(closeComponent)
    }

    fun newBlankTab() {
        calculateWidth()
        val homeComponent = HomeComponent(this)
        components.add(homeComponent)
        currentComponent = homeComponent
    }

    /**
     * @param component a need load component, the loading operations in a concurrent process,
     * render the [LoadableComponent] first, when load finished then render the [LoadableComponent]
     */
    fun openTab(component: LoadableComponent) {
        calculateWidth()
        val progressViewModel = LoadProgressViewModel()
        val loadProgressComponent = LoadProgressComponent(progressViewModel)
        doOpenTab(loadProgressComponent)

        progressViewModel.onSuccess = {
            doOpenTab(component)
        }
        launch {
            try {
                component.load()
                progressViewModel.success()
            } catch (e: Exception) {
                e.printStackTrace()
                progressViewModel.loading = false
                progressViewModel.fail(e.message)
            } finally {
                components.remove(loadProgressComponent)
            }
        }
    }

    private fun doOpenTab(component: AbstractComponent) {
        components.add(component)
        components.remove(currentComponent)
        currentComponent = component
    }

    private fun calculateWidth() {
        val windowWidth = windowState.size.width.value - (tabbarPaddingStart + tabbarPaddingEnd + iconSize)
        if ((components.size + 1) * tabWidth > windowWidth) {
            tabWidth = (windowWidth / (components.size + 1)).toInt()
        }
    }

    override fun clear() {
        coroutineContext.cancel()
        components.forEach { it.close() }
    }
}
package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.common.LoadableComponent
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.ui.progress.LoadProgressComponent
import org.spreadme.pdfgadgets.ui.progress.LoadProgressViewModel
import kotlin.system.exitProcess

class ApplicationFrameViewModel : ViewModel {

    val components = mutableStateListOf<AbstractComponent>()
    var currentComponent by mutableStateOf<AbstractComponent?>(null)

    //UI State
    var windowState: WindowState = WindowState()
    var isDark by mutableStateOf(false)
    var tabbarPaddingStart by mutableStateOf(16)
    var tabbarPaddingEnd by mutableStateOf(16)
    val iconSize by mutableStateOf(32)
    var tabWidth by mutableStateOf(168)

    fun addComponent(component: AbstractComponent) {
        calculateWidth()
        if (component is LoadableComponent) {
            asyncAddComponent(component)
        } else {
            components.add(component)
            currentComponent = component
        }
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

    /**
     * @param component a need load component, the loading operations in a concurrent process,
     * render the [LoadableComponent] first, when load finished then render the [LoadableComponent]
     */
    private fun asyncAddComponent(component: LoadableComponent) {
        val progressViewModel = LoadProgressViewModel()
        val loadProgressComponent = LoadProgressComponent(progressViewModel)
        progressViewModel.onSuccess = {
            components.add(component)
            currentComponent = component
        }
        MainScope().launch {
            try {
                component.load()
                progressViewModel.success()
            } catch (e: Exception) {
                progressViewModel.loading = false
                progressViewModel.fail(e.message)
            } finally {
                components.remove(loadProgressComponent)
            }
        }
        components.add(loadProgressComponent)
        currentComponent = loadProgressComponent
    }

    private fun calculateWidth() {
        val windowWidth = windowState.size.width.value - (tabbarPaddingStart + tabbarPaddingEnd + iconSize)
        if ((components.size + 1) * tabWidth > windowWidth) {
            tabWidth = (windowWidth / (components.size + 1)).toInt()
        }
    }
}
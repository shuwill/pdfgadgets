package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.*
import mu.KotlinLogging
import org.koin.core.component.KoinComponent
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.common.LoadableComponent
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.repository.AppConfigRepository
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.ui.home.HomeComponent
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class ApplicationViewModel(
    private val appConfigRepository: AppConfigRepository,
    private val fileMetadataRepository: FileMetadataRepository
) : ViewModel, CoroutineScope, KoinComponent {

    private val logger = KotlinLogging.logger {}

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
        //TODO re calculate the tab width
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
        val homeComponent = HomeComponent()
        components.add(homeComponent)
        currentComponent = homeComponent
    }

    /**
     * @param progressViewModel progrss view model
     * @param component a need load component, the loading operations in a concurrent process,
     * render the [LoadableComponent] first, when load finished then render the [LoadableComponent]
     */
    fun openFile(
        progressViewModel: LoadProgressViewModel,
        component: LoadableComponent<Path>,
    ) {
        progressViewModel.start()
        launch {
            try {
                component.load()
                progressViewModel.success()
            } catch (e: Exception) {
                logger.error(e.message, e)
                fileMetadataRepository.deleteByPath(component.content())
                val message = when (e) {
                    is NoSuchFileException -> "文件已被删除或被转移"
                    else -> e.message ?: "Pdf文件解析失败"
                }
                progressViewModel.fail(message)
            }
        }
        progressViewModel.onSuccess = {
            components.add(component)
            components.remove(currentComponent)
            currentComponent = component
        }
    }

    fun createFile() {
        //TODO create pdf file from support file type
    }

    fun calculateWidth() {
        val windowWidth = windowState.size.width.value - (tabbarPaddingStart + tabbarPaddingEnd + iconSize)
        if ((components.size + 1) * tabWidth > windowWidth) {
            tabWidth = (windowWidth / (components.size + 1)).toInt()
        }
    }

    override fun clear() {
        coroutineContext.cancel()
        components.forEach { it.close() }
    }

    fun config(configKey: String, configValue: String) {
        launch {
            appConfigRepository.config(configKey, configValue)
        }
    }
}
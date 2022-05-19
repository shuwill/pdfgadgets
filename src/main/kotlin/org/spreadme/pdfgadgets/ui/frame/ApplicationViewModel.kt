package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mu.KotlinLogging
import org.spreadme.pdfgadgets.common.AppComponent
import org.spreadme.pdfgadgets.common.LoadableAppComponent
import org.spreadme.pdfgadgets.common.ViewModel
import org.spreadme.pdfgadgets.common.viewModelScope
import org.spreadme.pdfgadgets.config.AppConfig
import org.spreadme.pdfgadgets.repository.AppConfigRepository
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.ui.home.HomeComponent
import java.nio.file.Path
import kotlin.system.exitProcess

class ApplicationViewModel(
    private val appConfigRepository: AppConfigRepository,
    private val fileMetadataRepository: FileMetadataRepository
) : ViewModel() {

    private val logger = KotlinLogging.logger {}

    var composeWindow = ComposeWindow()

    val components = mutableStateListOf<AppComponent>()
    var currentComponent by mutableStateOf<AppComponent?>(null)

    //application load status
    var finished by mutableStateOf(false)
    var loadMessage = MutableStateFlow("")

    //UI State
    var windowState = WindowState()
    var isDark by AppConfig.isDark
    var tabWidth by mutableStateOf(168)

    var isCustomWindowDecoration = false
    var tabbarPaddingStart = 0
    var tabbarPaddingEnd = 16
    val addIconSize = 32

    fun customWindowDecoration(enabled: Boolean) {
        isCustomWindowDecoration = enabled
        tabbarPaddingStart = 80
    }

    fun onWindowStateChange(size: DpSize) {
        //TODO re calculate the tab width
    }

    fun onSelectTab(selectedComponent: AppComponent) {
        currentComponent = selectedComponent
    }

    fun onCloseTab(closeComponent: AppComponent) {
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
        val homeComponent = HomeComponent(this)
        components.add(homeComponent)
        currentComponent = homeComponent
    }

    fun openCurrentTab(component: AppComponent) {
        components.add(component)
        components.remove(currentComponent)
        currentComponent = component
    }

    fun calculateTabWidth() {
        val windowWidth = windowState.size.width.value - (tabbarPaddingStart + tabbarPaddingEnd + addIconSize)
        if ((components.size + 1) * tabWidth > windowWidth) {
            tabWidth = (windowWidth / (components.size + 1)).toInt()
        }
    }

    /**
     * @param progressViewModel progrss view model
     * @param component a need load component, the loading operations in a concurrent process,
     * render the [LoadableAppComponent] first, when load finished then render the [LoadableAppComponent]
     */
    fun openFile(
        progressViewModel: LoadProgressViewModel,
        component: LoadableAppComponent<Path>,
    ) {
        progressViewModel.onSuccess = {
            openCurrentTab(component)
        }
        progressViewModel.start()
        viewModelScope.launch {
            try {
                component.load()
                progressViewModel.success()
            } catch (e: Exception) {
                logger.error(e.message, e)
                fileMetadataRepository.deleteByPath(component.content())
                val message = when (e) {
                    is java.nio.file.NoSuchFileException -> "文件已被删除或被转移"
                    else -> e.message ?: "Pdf文件解析失败"
                }
                progressViewModel.fail(message)
                progressViewModel.onFail()
            }
        }
    }

    fun createFile() {
        //TODO create pdf file from support file type
    }


    fun config(configKey: String, configValue: String) {
        viewModelScope.launch {
            appConfigRepository.config(configKey, configValue)
        }
    }

    fun loadConfig() {
        viewModelScope.launch {
            appConfigRepository.load(this@ApplicationViewModel.loadMessage)
            newBlankTab()
            this@ApplicationViewModel.finished = true
        }
    }

    override fun onCleared() {
        components.forEach {
            it.close()
        }
    }
}
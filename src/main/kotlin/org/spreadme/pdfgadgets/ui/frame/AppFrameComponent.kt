package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.AppComponent
import org.spreadme.pdfgadgets.repository.AppConfigRepository
import org.spreadme.pdfgadgets.repository.FileMetadataParser
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.repository.PdfMetadataParser
import org.spreadme.pdfgadgets.ui.common.CustomWindowDecoration
import org.spreadme.pdfgadgets.ui.tabbars.Tabbars
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors

class AppFrameComponent : AppComponent("Application Frame") {

    private val appConfigRepository by inject<AppConfigRepository>()
    private val fileMetadataRepository by inject<FileMetadataRepository>()
    private val fileMetadataParser by inject<FileMetadataParser>()
    private val pdfMetadataParser by inject<PdfMetadataParser>()

    private val applicationViewModel = getViewModel<ApplicationViewModel>(
        appConfigRepository, fileMetadataRepository,
        fileMetadataParser, pdfMetadataParser
    )

    @Composable
    override fun onRender() {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
            //Tabs Bar
            if (applicationViewModel.isCustomWindowDecoration) {
                CustomDecorationTabBars(applicationViewModel)
            } else {
                DefaultTabBars(applicationViewModel)
            }
            Divider(color = LocalExtraColors.current.border, thickness = 1.dp)
            Box(
                Modifier.fillMaxSize()
            ) {
                //Tabs View
                TabView(applicationViewModel)
            }
        }
    }

    fun viewModel(): ApplicationViewModel = applicationViewModel
}

@Composable
fun CustomDecorationTabBars(
    applicationViewModel: ApplicationViewModel
) {
    CustomWindowDecoration(
        Modifier.fillMaxWidth().height(40.dp).background(MaterialTheme.colors.primaryVariant)
            .padding(
                start = applicationViewModel.tabbarPaddingStart.dp,
                end = applicationViewModel.tabbarPaddingEnd.dp
            ),
        horizontalArrangement = Arrangement.Start,
        windowState = applicationViewModel.windowState,
    ) {
        Tabbars(
            applicationViewModel.components,
            applicationViewModel.currentComponent,
            tabWidthProvider = applicationViewModel::calculateTabWidth,
            addIconSize = applicationViewModel.addIconSize,
            onSelected = applicationViewModel::onSelectTab,
            onClose = applicationViewModel::onCloseTab,
        ) {
            applicationViewModel.newBlankTab()
            applicationViewModel.calculateTabWidth()
        }
    }
}

@Composable
fun DefaultTabBars(
    applicationViewModel: ApplicationViewModel
) {
    Tabbars(
        applicationViewModel.components,
        applicationViewModel.currentComponent,
        Modifier.fillMaxWidth().height(40.dp).background(MaterialTheme.colors.primaryVariant)
            .padding(
                start = applicationViewModel.tabbarPaddingStart.dp,
                end = applicationViewModel.tabbarPaddingEnd.dp
            ),
        applicationViewModel::calculateTabWidth,
        addIconSize = applicationViewModel.addIconSize,
        onSelected = applicationViewModel::onSelectTab,
        onClose = applicationViewModel::onCloseTab,
    ) {
        applicationViewModel.newBlankTab()
        applicationViewModel.calculateTabWidth()
    }
}

@Composable
fun TabView(frameViewModel: ApplicationViewModel) {
    frameViewModel.currentComponent?.render()
}

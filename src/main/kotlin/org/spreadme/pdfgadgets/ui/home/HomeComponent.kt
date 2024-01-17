package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mu.KotlinLogging
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.AppComponent
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel
import org.spreadme.pdfgadgets.ui.frame.LoadProgressViewModel
import org.spreadme.pdfgadgets.ui.frame.MainApplicationFrame
import org.spreadme.pdfgadgets.ui.toolbars.ToolbarsViewModel

class HomeComponent(
    private val applicationViewModel: ApplicationViewModel
) : AppComponent("新建标签") {

    private val logger = KotlinLogging.logger {}

    private val fileMetadataRepository by inject<FileMetadataRepository>()

    private val loadProgressViewModel = getViewModel<LoadProgressViewModel>()
    private val recentFileViewModel = getViewModel<RecentFileViewModel>(fileMetadataRepository)

    @Composable
    override fun onRender() {
        loadProgressViewModel.onFail = {
            recentFileViewModel.reacquire()
        }
        MainApplicationFrame(
            applicationViewModel,
        ) {
            logger.info("home component【${uid}】rendered")
            Column(Modifier.fillMaxSize()) {
                recentFileViewModel.load()
                RecentFiles(recentFileViewModel) {
                    applicationViewModel.openFile(
                        it.path(),
                        loadProgressViewModel
                    )
                }
            }
        }
    }

}
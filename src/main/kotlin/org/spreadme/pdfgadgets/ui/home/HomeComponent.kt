package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.AppComponent
import org.spreadme.pdfgadgets.common.getViewModel
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel
import org.spreadme.pdfgadgets.ui.frame.LoadProgressViewModel
import org.spreadme.pdfgadgets.ui.frame.MainApplicationFrame
import org.spreadme.pdfgadgets.ui.pdfview.PdfViewAppComponent
import org.spreadme.pdfgadgets.ui.toolbars.ToolbarsViewModel

class HomeComponent(
    private val applicationViewModel: ApplicationViewModel
) : AppComponent("新建标签") {

    private val fileMetadataRepository by inject<FileMetadataRepository>()

    private val toolbarsViewModel = getViewModel<ToolbarsViewModel>(false)
    private val loadProgressViewModel = getViewModel<LoadProgressViewModel>()
    private val recentFileViewModel = getViewModel<RecentFileViewModel>(fileMetadataRepository)

    @Composable
    override fun onRender() {
        loadProgressViewModel.onFail = {
            recentFileViewModel.reacquire()
        }
        MainApplicationFrame(
            toolbarsViewModel,
            applicationViewModel,
            loadProgressViewModel
        ) {
            println("home component【${uid}】rendered")
            Column(Modifier.fillMaxSize()) {
                val recentFileState = remember { recentFileViewModel }
                RecentFiles(recentFileState) {
                    applicationViewModel.openFile(
                        loadProgressViewModel,
                        PdfViewAppComponent(it.path(), applicationViewModel)
                    )
                }
            }
        }
    }

}
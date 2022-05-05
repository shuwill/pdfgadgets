package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.AbstractComponent
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel
import org.spreadme.pdfgadgets.ui.frame.LoadProgressViewModel
import org.spreadme.pdfgadgets.ui.frame.MainApplicationFrame
import org.spreadme.pdfgadgets.ui.pdfview.PdfViewComponent
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel
import java.nio.file.Paths

class HomeComponent : AbstractComponent("新建标签"), KoinComponent {

    private val applicationViewModel by inject<ApplicationViewModel>()
    private val fileMetadataRepository by inject<FileMetadataRepository>()

    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(false)
    private val loadProgressViewModel: LoadProgressViewModel = LoadProgressViewModel()
    private val recentFileViewModel: RecentFileViewModel = RecentFileViewModel(fileMetadataRepository)

    @Composable
    override fun doRender() {
        loadProgressViewModel.onFail = {
            recentFileViewModel.reacquire()
        }
        MainApplicationFrame(
            toolbarViewModel,
            applicationViewModel,
            loadProgressViewModel
        ) {
            println("home component【${uid}】rendered")
            Column(Modifier.fillMaxSize()) {
                val recentFileState = remember { recentFileViewModel }
                RecentFiles(recentFileState) {
                    applicationViewModel.openFile(
                        loadProgressViewModel,
                        PdfViewComponent(Paths.get(it.path))
                    )
                }
            }
        }
    }

}
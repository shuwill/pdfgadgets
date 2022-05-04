package org.spreadme.pdfgadgets.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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
import kotlin.coroutines.CoroutineContext

class HomeComponent : AbstractComponent("新建标签"), CoroutineScope, KoinComponent {

    private val applicationViewModel by inject<ApplicationViewModel>()
    private val fileMetadataRepository by inject<FileMetadataRepository>()

    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(false)
    private val loadProgressViewModel: LoadProgressViewModel = LoadProgressViewModel()
    private val recentFileViewModel: RecentFileViewModel = RecentFileViewModel(fileMetadataRepository)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + SupervisorJob()

    @Composable
    override fun doRender() {
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

    override fun close() {
        coroutineContext.cancel()
    }
}
package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.LoadableComponent
import org.spreadme.pdfgadgets.repository.FileMetadataParser
import org.spreadme.pdfgadgets.repository.FileMetadataRepository
import org.spreadme.pdfgadgets.repository.PdfMetadataParser
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel
import org.spreadme.pdfgadgets.ui.frame.LoadProgressViewModel
import org.spreadme.pdfgadgets.ui.frame.MainApplicationFrame
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanel
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel
import java.nio.file.Path

class PdfViewComponent(filePath: Path) : LoadableComponent(), KoinComponent {

    val path: Path = filePath

    private val applicationViewModel by inject<ApplicationViewModel>()
    private val fileMetadataRepository by inject<FileMetadataRepository>()
    private val fileMetadataParser by inject<FileMetadataParser>()
    private val pdfMetadataParser by inject<PdfMetadataParser>()

    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(true)
    private val loadProgressViewModel: LoadProgressViewModel = LoadProgressViewModel()
    private lateinit var pdfViewModel: PdfViewModel

    @Composable
    override fun doRender() {
        MainApplicationFrame(
            toolbarViewModel,
            applicationViewModel,
            loadProgressViewModel
        ) {
            println("pdf view component【${name}】rendered")
            val pdfpdfViewModel = remember { pdfViewModel }
            toolbarViewModel.onChangeSideViewMode = pdfpdfViewModel::onChangeSideViewMode
            toolbarViewModel.onChangeScale = pdfpdfViewModel::onChangeScalue
            SidePanelGroup(pdfpdfViewModel)
            PageDetailGroup(pdfpdfViewModel)
        }
    }

    @Composable
    fun RowScope.SidePanelGroup(pdfViewModel: PdfViewModel) {
        // PDF Info View Component
        if (pdfViewModel.hasSideView(SidePanelMode.INFO)) {
            DocumentAttrDetail(
                pdfViewModel.pdfMetadata.fileMetadata.name,
                pdfViewModel.pdfMetadata.documentInfo
            ) {
                pdfViewModel.onChangeSideViewMode(SidePanelMode.INFO)
            }
        }

        // PDF Bookmarks View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SidePanelMode.OUTLINES)) {
            SidePanel(pdfViewModel.sideViewModel(SidePanelMode.OUTLINES)) {
                OutlinesTree(
                    it,
                    pdfViewModel.pdfMetadata.outlines,
                    pdfViewModel.pdfMetadata.pages,
                    pdfViewModel::onScroll
                )
            }
        }

        // PDF Structure View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SidePanelMode.STRUCTURE)) {
            SidePanel(pdfViewModel.sideViewModel(SidePanelMode.STRUCTURE)) {
                StructureTree(pdfViewModel.pdfMetadata.structureRoot, it)
            }
        }

        // PDF Signature View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SidePanelMode.SIGNATURE)) {
            SidePanel(pdfViewModel.sideViewModel(SidePanelMode.SIGNATURE)) {
                SignatureList(
                    pdfViewModel.pdfMetadata.signatures,
                    it,
                    pdfViewModel::onScroll
                )
            }
        }
    }

    @Composable
    fun PageDetailGroup(
        pdfViewModel: PdfViewModel
    ) {
        val lazyListState = rememberLazyListState(pdfViewModel.initScrollIndex, pdfViewModel.initScrollOffset)
        val horizontalScollState = rememberScrollState(pdfViewModel.horizontalInitScollIndex)

        val coroutineScope = rememberCoroutineScope()
        if (pdfViewModel.scrollable) {
            coroutineScope.launch {
                lazyListState.scrollToItem(pdfViewModel.scrollIndex, pdfViewModel.scrollOffset)
                pdfViewModel.scrollFinish()
                pdfViewModel.scrollable = false
            }
        }

        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.surface)) {
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
                    .padding(end = 16.dp, bottom = 16.dp)
                    .horizontalScroll(horizontalScollState),
                state = lazyListState
            ) {
                itemsIndexed(pdfViewModel.pdfMetadata.pages) { _, item ->
                    PageDetail(item, pdfViewModel)
                }
            }

            pdfViewModel.initScrollIndex = lazyListState.firstVisibleItemIndex
            pdfViewModel.initScrollOffset = lazyListState.firstVisibleItemScrollOffset
            pdfViewModel.horizontalInitScollIndex = horizontalScollState.value

            Box(
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
                    .size(80.dp, 32.dp)
                    .background(MaterialTheme.colors.primary.copy(0.85f), RoundedCornerShape(4.dp))
                    .align(Alignment.BottomEnd)
                    .zIndex(2f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${lazyListState.firstVisibleItemIndex + 1} / ${pdfViewModel.pdfMetadata.numberOfPages}",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onPrimary
                )
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(lazyListState)
            )
            HorizontalScrollbar(
                modifier = Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(end = 16.dp),
                adapter = rememberScrollbarAdapter(horizontalScollState)
            )
        }
    }

    override suspend fun load() {
        val fileMetadata = fileMetadataParser.parse(path)
        fileMetadataRepository.save(fileMetadata)
        name = fileMetadata.name
        val pdfMetadata = pdfMetadataParser.parse(fileMetadata)
        pdfViewModel = PdfViewModel(pdfMetadata)
    }

    override fun close() {
        pdfViewModel.clear()
    }
}
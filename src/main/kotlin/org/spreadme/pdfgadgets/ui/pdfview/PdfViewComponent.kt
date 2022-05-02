package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.spreadme.pdfgadgets.common.LoadableComponent
import org.spreadme.pdfgadgets.repository.FileMetadataParser
import org.spreadme.pdfgadgets.repository.PdfMetadataParser
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.side.SideView
import org.spreadme.pdfgadgets.ui.side.SideViewMode
import org.spreadme.pdfgadgets.ui.toolbar.Toolbar
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel
import java.nio.file.Path

class PdfViewComponent(
    filePath: Path
) : LoadableComponent(), KoinComponent {

    val path: Path = filePath

    private val fileMetadataParser by inject<FileMetadataParser>()
    private val pdfMetadataParser by inject<PdfMetadataParser>()

    private val toolbarViewModel: ToolbarViewModel = ToolbarViewModel(true)
    private lateinit var pdfViewModel: PdfViewModel

    @Composable
    override fun doRender() {
        Column(
            Modifier.fillMaxSize().background(MaterialTheme.colors.surface),
            verticalArrangement = Arrangement.Top
        ) {
            println("pdf view component [$name] rendered")
            Toolbar(toolbarViewModel = toolbarViewModel)
            Divider(color = MaterialTheme.colors.primaryVariant)
            val focusManager = LocalFocusManager.current
            Row(Modifier.fillMaxSize().clickable { focusManager.clearFocus() }) {
                val pdfpdfViewModel = remember { pdfViewModel }
                toolbarViewModel.onChangeSideViewMode = pdfpdfViewModel::onChangeSideViewMode
                toolbarViewModel.onChangeScale = pdfpdfViewModel::onChangeScalue
                SideViewGroup(pdfpdfViewModel)
                PageDetailGroup(pdfpdfViewModel)
            }
        }
    }

    @Composable
    fun RowScope.SideViewGroup(pdfViewModel: PdfViewModel) {
        // PDF Info View Component
        if (pdfViewModel.hasSideView(SideViewMode.INFO)) {
            DocumentAttrDetail(
                pdfViewModel.pdfMetadata.fileMetadata.name,
                pdfViewModel.pdfMetadata.documentInfo
            ) {
                pdfViewModel.onChangeSideViewMode(SideViewMode.INFO)
            }
        }

        // PDF Bookmarks View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SideViewMode.OUTLINES)) {
            SideView(pdfViewModel.sideViewModel(SideViewMode.OUTLINES)) {
                OutlinesTree(
                    it,
                    pdfViewModel.pdfMetadata.outlines,
                    pdfViewModel.pdfMetadata.pages,
                    pdfViewModel::onScroll
                )
            }
        }

        // PDF Structure View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SideViewMode.STRUCTURE)) {
            SideView(pdfViewModel.sideViewModel(SideViewMode.STRUCTURE)) {
                StructureTree(pdfViewModel.pdfMetadata.structureRoot, it)
            }
        }

        // PDF Signature View Component
        AnimatedVisibility(pdfViewModel.hasSideView(SideViewMode.SIGNATURE)) {
            SideView(pdfViewModel.sideViewModel(SideViewMode.SIGNATURE)) {
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
        val lazyListState = rememberLazyListState(pdfViewModel.scrollIndex, pdfViewModel.scrollOffset)
        val horizontalScollState = rememberScrollState(0)

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

            Box(
                modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
                    .size(80.dp, 32.dp)
                    .background(MaterialTheme.colors.background.copy(0.85f), RoundedCornerShape(4.dp))
                    .align(Alignment.BottomEnd)
                    .zIndex(2f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "${lazyListState.firstVisibleItemIndex + 1} / ${pdfViewModel.pdfMetadata.numberOfPages}",
                    style = MaterialTheme.typography.subtitle2,
                    color = MaterialTheme.colors.onBackground
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
        name = fileMetadata.name
        val pdfMetadata = pdfMetadataParser.parse(fileMetadata)
        pdfViewModel = PdfViewModel(pdfMetadata)
    }

    override fun close() {
        pdfViewModel.clear()
    }
}
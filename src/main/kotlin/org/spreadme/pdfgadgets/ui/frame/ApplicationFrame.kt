package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.PlatformUI
import org.spreadme.pdfgadgets.ui.common.CustomWindowDecoration
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.home.HomeComponent
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun ApplicationFrame(
    window: ComposeWindow,
    frameViewModel: ApplicationFrameViewModel
) {
    // custom the window title
    val platformUI = PlatformUI(window.rootPane, frameViewModel.isDark)
    if (platformUI.isSupportCustomWindowDecoration()) {
        platformUI.customWindowDecoration()
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        //Tabs Bar
        if (platformUI.isCustomWindowDecoration()) {
            frameViewModel.tabbarPaddingStart = 80
            CustomWindowDecoration(
                Modifier.fillMaxWidth().height(40.dp).background(MaterialTheme.colors.primaryVariant)
                    .padding(start = frameViewModel.tabbarPaddingStart.dp, end = frameViewModel.tabbarPaddingEnd.dp),
                horizontalArrangement = Arrangement.Start,
                windowState = frameViewModel.windowState,
            ) {
                TabsBar(frameViewModel = frameViewModel)
            }
        } else {
            TabsBar(
                Modifier.fillMaxWidth().height(40.dp).background(MaterialTheme.colors.primaryVariant)
                    .padding(start = frameViewModel.tabbarPaddingStart.dp, end = frameViewModel.tabbarPaddingEnd.dp),
                frameViewModel
            )
        }
        Divider(color = MaterialTheme.colors.primaryVariant, thickness = 1.dp)
        Box(
            Modifier.fillMaxSize()
        ) {
            //Tabs View
            TabView(frameViewModel)
            //Theme change View
            ThemeSelector(
                Modifier.padding(start = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomStart)
                    .zIndex(2f),
                frameViewModel
            )
        }
    }
}

@Composable
fun TabsBar(
    modifier: Modifier = Modifier,
    frameViewModel: ApplicationFrameViewModel
) {
    LazyRow(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(frameViewModel.components) { index, item ->
            TabItem(
                Modifier.fillMaxHeight().width(frameViewModel.tabWidth.dp),
                title = item.name,
                active = frameViewModel.currentComponent == item,
                onSelected = {
                    frameViewModel.onSelectTab(item)
                },
                onClose = {
                    frameViewModel.onClose(item)
                }
            )
            // if item is last, show add icon
            if (index == frameViewModel.components.size - 1) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(frameViewModel.iconSize.dp).padding(horizontal = 8.dp).clickable {
                        val homeComponent = HomeComponent(frameViewModel)
                        frameViewModel.addComponent(homeComponent)
                    }
                )
            }
        }
    }
}

@Composable
fun TabItem(
    modifier: Modifier = Modifier,
    title: String,
    active: Boolean,
    onSelected: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        modifier.background(
            active.choose(
                MaterialTheme.colors.background,
                MaterialTheme.colors.primaryVariant
            )
        ).clickable {
            onSelected()
        }.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            title,
            color = active.choose(MaterialTheme.colors.onBackground, MaterialTheme.colors.onPrimary),
            style = MaterialTheme.typography.caption,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )

        Box(
            Modifier.fillMaxSize().zIndex(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close",
                tint = active.choose(MaterialTheme.colors.onBackground, MaterialTheme.colors.onPrimary),
                modifier = Modifier.size(16.dp).clickable {
                    onClose()
                }
            )
        }

    }
}

@Composable
fun ThemeSelector(
    modifier: Modifier = Modifier,
    viewModel: ApplicationFrameViewModel
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(
                viewModel.isDark.choose(
                    R.Icons.dark,
                    R.Icons.lignt
                )
            ),
            contentDescription = "Theme Selector",
            tint = MaterialTheme.colors.onSurface,
            modifier = Modifier.size(24.dp).clickable {
                viewModel.isDark = !viewModel.isDark
            }
        )
    }
}

@Composable
fun TabView(frameViewModel: ApplicationFrameViewModel) {
    frameViewModel.currentComponent?.render()
}

@Composable
@Preview
fun TabItemPreview() {
    TabItem(
        Modifier.size(168.dp, 40.dp),
        title = "测试Tab页",
        active = true,
        onSelected = {

        },
        onClose = {

        }
    )
}
package org.spreadme.pdfgadgets.ui.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.DropdownTextInputField
import org.spreadme.pdfgadgets.ui.common.TextSearchInputField
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.ui.toolbar.ToolbarViewModel.Companion.SCALES

@Composable
fun Toolbar(
    modifier: Modifier = Modifier.fillMaxWidth().height(48.dp)
        .background(MaterialTheme.colors.background),
    toolbarViewModel: ToolbarViewModel
) {
    Row(
        modifier
    ) {
        val tint = if (toolbarViewModel.enabled) {
            MaterialTheme.colors.onBackground
        } else {
            LocalExtraColors.current.iconDisable
        }
        val contentColor = if (toolbarViewModel.enabled) {
            MaterialTheme.colors.onSurface
        } else {
            LocalExtraColors.current.contentDisable
        }
        ViewModeBar(
            Modifier.fillMaxSize().weight(0.2f),
            toolbarViewModel,
            tint = tint
        )
        ScaleBar(
            Modifier.fillMaxSize().weight(0.6f),
            toolbarViewModel,
            tint = tint,
            contentColor = contentColor
        )
        TextSearchBar(
            Modifier.fillMaxSize().weight(0.2f),
            toolbarViewModel, contentColor
        )
    }
}

@Composable
fun ViewModeBar(
    modifier: Modifier = Modifier,
    toolbarViewModel: ToolbarViewModel,
    tint: Color,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val viewModelState = remember { toolbarViewModel }
        SidePanelMode.values().forEach { mode ->
            Icon(
                painter = painterResource(mode.icon),
                contentDescription = mode.desc,
                tint = tint,
                modifier = Modifier.padding(start = 16.dp)
                    .size(16.dp)
                    .clickable(viewModelState.enabled) {
                        viewModelState.changeSideViewMode(mode)
                    }
            )
        }
    }
}

@Composable
fun ScaleBar(
    modifier: Modifier = Modifier,
    toolbarViewModel: ToolbarViewModel,
    tint: Color,
    contentColor: Color,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val viewModelState = remember { toolbarViewModel }
        Icon(
            painterResource(R.Icons.zoom_in),
            contentDescription = "Zoom In",
            tint = tint,
            modifier = Modifier.size(16.dp).clickable(viewModelState.enabled) {
                viewModelState.changeScale(ScaleType.ZOOM_IN)
            }
        )
        DropdownTextInputField(
            "${viewModelState.scale}%",
            SCALES,
            modifier = Modifier
                .padding(8.dp, 8.dp)
                .fillMaxHeight().width(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface),
            MaterialTheme.typography.caption.copy(color = contentColor),
            tint = contentColor,
            enabled = viewModelState.enabled
        ) {
            viewModelState.changeScale(null, it.replace("%", "").toInt())
        }
        Icon(
            painterResource(R.Icons.zoom_out),
            contentDescription = "Zoom In",
            tint = tint,
            modifier = Modifier.size(16.dp).clickable(viewModelState.enabled) {
                viewModelState.changeScale(ScaleType.ZOOM_OUT)
            }
        )
    }
}

@Composable
fun TextSearchBar(
    modifier: Modifier = Modifier,
    toolbarViewModel: ToolbarViewModel,
    contentColor: Color,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val viewModelState = remember { toolbarViewModel }
        TextSearchInputField(
            viewModelState.searchKeyword,
            onValueChange = {
                viewModelState.searchKeyword = it
            },
            modifier = Modifier.padding(16.dp, 8.dp)
                .fillMaxHeight().widthIn(240.dp, 272.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface),
            textStyle = MaterialTheme.typography.caption.copy(color = contentColor),
            tint = contentColor,
            enabled = viewModelState.enabled
        )
    }
}

@Composable
@Preview
fun ViewBarPreview() {
    ViewModeBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        ToolbarViewModel(),
        tint = MaterialTheme.colors.onBackground,
    )
}

@Composable
@Preview
fun ScaleBarPreview() {
    ScaleBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        ToolbarViewModel(),
        tint = MaterialTheme.colors.onBackground,
        contentColor = MaterialTheme.colors.onSurface
    )
}
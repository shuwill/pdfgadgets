package org.spreadme.pdfgadgets.ui.toolbar

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    modifier: Modifier = Modifier,
    toolbarViewModel: ToolbarViewModel
) {
    Row(
        modifier
    ) {
        SidePanelModeBar(
            Modifier.fillMaxSize().weight(0.2f),
            toolbarViewModel.enabled,
            onSidePanelModeChange = toolbarViewModel::changeSideViewMode
        )
        ScaleBar(
            Modifier.fillMaxSize().weight(0.6f),
            toolbarViewModel.enabled,
            toolbarViewModel.scale,
            onScaleTypeChange = toolbarViewModel::changeScale,
            onScaleChange = toolbarViewModel::changeScale
        )
        TextSearchBar(
            toolbarViewModel.searchKeyword,
            Modifier.fillMaxSize().weight(0.2f),
            toolbarViewModel.enabled
        ) {
            toolbarViewModel.searchKeyword = it
        }
    }
}

@Composable
fun SidePanelModeBar(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onSidePanelModeChange: (SidePanelMode) -> Unit
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SidePanelMode.values().forEach { mode ->
            Icon(
                painter = painterResource(mode.icon),
                contentDescription = mode.desc,
                tint = if (enabled) {
                    MaterialTheme.colors.onBackground
                } else {
                    LocalExtraColors.current.iconDisable
                },
                modifier = Modifier.padding(start = 16.dp)
                    .size(16.dp)
                    .clickable(enabled) {
                        onSidePanelModeChange(mode)
                    }
            )
        }
    }
}

@Composable
fun ScaleBar(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    scale: Int,
    onScaleTypeChange: (ScaleType) -> Unit,
    onScaleChange: (Int) -> Unit
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ScaleIcon(
            ScaleType.ZOOM_IN,
            iconResource = R.Icons.zoom_in,
            enabled = enabled,
            onScaleChange = onScaleTypeChange
        )
        ScaleDropdownInput(
            scale,
            enabled = enabled,
            onScaleChange = onScaleChange
        )
        ScaleIcon(
            ScaleType.ZOOM_OUT,
            iconResource = R.Icons.zoom_out,
            enabled = enabled,
            onScaleChange = onScaleTypeChange
        )
    }
}

@Composable
fun ScaleIcon(
    scaleType: ScaleType,
    size: Int = 16,
    iconResource: String,
    enabled: Boolean,
    onScaleChange: (ScaleType) -> Unit
) {
    Icon(
        painterResource(iconResource),
        contentDescription = "Zoom In",
        tint = if (enabled) {
            MaterialTheme.colors.onBackground
        } else {
            LocalExtraColors.current.iconDisable
        },
        modifier = Modifier.size(size.dp).clickable(enabled) {
            onScaleChange(scaleType)
        }
    )
}

@Composable
fun ScaleDropdownInput(
    scale: Int,
    enabled: Boolean,
    onScaleChange: (Int) -> Unit
) {
    DropdownTextInputField(
        "${scale}%",
        SCALES,
        modifier = Modifier
            .padding(8.dp, 8.dp)
            .fillMaxHeight().width(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface),
        MaterialTheme.typography.caption.copy(
            color = if (enabled) {
                MaterialTheme.colors.onSurface
            } else {
                LocalExtraColors.current.contentDisable
            }
        ),
        tint = if (enabled) {
            MaterialTheme.colors.onBackground
        } else {
            LocalExtraColors.current.iconDisable
        },
        enabled = enabled
    ) {
        onScaleChange(it.replace("%", "").toInt())
    }
}

@Composable
fun TextSearchBar(
    searchKeyword: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextSearchInputField(
            searchKeyword,
            onValueChange = onValueChange,
            modifier = Modifier.padding(16.dp, 8.dp)
                .fillMaxHeight().widthIn(240.dp, 272.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface),
            textStyle = MaterialTheme.typography.caption.copy(
                color = if (enabled) {
                    MaterialTheme.colors.onSurface
                } else {
                    LocalExtraColors.current.contentDisable
                }
            ),
            tint = if (enabled) {
                MaterialTheme.colors.onBackground
            } else {
                LocalExtraColors.current.iconDisable
            },
            enabled = enabled
        )
    }
}

@Composable
@Preview
fun ViewBarPreview() {
    SidePanelModeBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        false,
    ) {

    }
}

@Composable
@Preview
fun ScaleBarPreview() {
    ScaleBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        true,
        100,
        onScaleTypeChange = {},
        onScaleChange = {}
    )
}
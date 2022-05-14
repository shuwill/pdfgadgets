package org.spreadme.pdfgadgets.ui.toolbars

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.model.Position
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.DropdownTextInputField
import org.spreadme.pdfgadgets.ui.common.TextSearchInputField
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.pdfview.PdfViewType
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelMode
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.ui.toolbars.ToolbarsViewModel.Companion.SCALES

@Composable
fun Toolbars(
    modifier: Modifier = Modifier,
    toolbarsViewModel: ToolbarsViewModel
) {
    Row(
        modifier
    ) {
        SidePanelSwitchBar(
            Modifier.fillMaxSize().weight(0.2f),
            toolbarsViewModel.enabled,
            onSidePanelModeChange = toolbarsViewModel::changeSideViewMode
        )
        OperableBar(
            Modifier.fillMaxSize().weight(0.8f),
            toolbarsViewModel.enabled,
            toolbarsViewModel.scale,
            onScaleTypeChange = toolbarsViewModel::changeScale,
            onScaleChange = toolbarsViewModel::changeScale,
            onViewTypeChange = toolbarsViewModel::changePdfViewType
        )
        TextSearchBar(
            toolbarsViewModel.searchKeyword,
            Modifier.fillMaxSize().weight(0.2f),
            toolbarsViewModel.enabled,
            toolbarsViewModel::searchText,
            toolbarsViewModel.position,
            toolbarsViewModel.currentIndex,
            toolbarsViewModel.onScroll,
            toolbarsViewModel::cleanSearch
        ) {
            toolbarsViewModel.searchKeyword = it
        }
    }
}

@Composable
fun SidePanelSwitchBar(
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
fun OperableBar(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    scale: Int,
    onScaleTypeChange: (ScaleType) -> Unit,
    onScaleChange: (Int) -> Unit,
    onViewTypeChange: (PdfViewType) -> Unit
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
        PdfViewTypeSwitchBar(
            Modifier.padding(start = 16.dp),
            enabled = enabled,
            onViewTypeChange
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
fun PdfViewTypeSwitchBar(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onViewTypeChange: (PdfViewType) -> Unit
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PdfViewType.values().forEach { type ->
            Icon(
                painterResource(type.icon),
                contentDescription = "",
                tint = if (enabled) {
                    MaterialTheme.colors.onBackground
                } else {
                    LocalExtraColors.current.iconDisable
                },
                modifier = Modifier.padding(start = 8.dp).size(16.dp).clickable{
                    onViewTypeChange(type)
                }
            )
        }
    }
}

@Composable
fun TextSearchBar(
    searchKeyword: String,
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onSeach: () -> Unit,
    positions: List<Position>,
    currentIndex: MutableState<Int>,
    onScroll: (postion: Position, scrollFinish: () -> Unit) -> Unit,
    onClean: () -> Unit,
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
                .background(MaterialTheme.colors.surface)
                .onPreviewKeyEvent {
                    if (it.key == Key.Enter && it.type == KeyEventType.KeyDown) {
                        onSeach()
                        true
                    } else {
                        false
                    }
                },
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
        ) {
            if (positions.isNotEmpty()) {
                SearchResultDetail(currentIndex, positions, onScroll)
            }
            if (searchKeyword.isNotEmpty()) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "",
                    tint = MaterialTheme.colors.onBackground,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp).size(12.dp)
                        .clickable {
                            onClean()
                        }
                )
            }
        }
    }
}

@Composable
fun SearchResultDetail(
    currentIndex: MutableState<Int> = mutableStateOf(0),
    positions: List<Position>,
    onScroll: (postion: Position, scrollFinish: () -> Unit) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "${currentIndex.value}/${positions.size}",
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.overline
        )

        Spacer(modifier = Modifier.padding(4.dp).fillMaxHeight().width(1.dp))
        Icon(
            Icons.Default.ArrowDropDown,
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(end = 4.dp).size(16.dp)
                .clickable {
                    if (currentIndex.value < positions.size) {
                        val position = positions[currentIndex.value++]
                        onScroll(position) {
                            position.selected.value = true
                        }
                    }
                }
        )
        Icon(
            Icons.Default.ArrowDropUp,
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(end = 4.dp).size(16.dp)
                .clickable {
                    if (currentIndex.value > 1) {
                        val position = positions[(--currentIndex.value) - 1]
                        onScroll(position) {
                            position.selected.value = true
                        }
                    }
                }
        )

    }
}

@Composable
@Preview
fun ViewBarPreview() {
    SidePanelSwitchBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        false,
    ) {

    }
}

@Composable
@Preview
fun ScaleBarPreview() {
    OperableBar(
        Modifier.fillMaxWidth().height(48.dp).background(MaterialTheme.colors.background),
        true,
        100,
        onScaleTypeChange = {},
        onScaleChange = {},
        onViewTypeChange = {}
    )
}
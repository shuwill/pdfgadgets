package org.spreadme.pdfgadgets.ui.tabbars

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.common.AppComponent
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun Tabbars(
    components: List<AppComponent>,
    currentComponent: AppComponent?,
    modifier: Modifier = Modifier,
    tabWidthProvider: () -> Float,
    addIconSize: Int,
    onSelected: (AppComponent) -> Unit,
    onClose: (AppComponent) -> Unit,
    onAdd: () -> Unit
) {
    LazyRow(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        itemsIndexed(components) { index, item ->
            TabItem(
                tabWidthProvider,
                title = item.name,
                active = currentComponent == item,
                onSelected = {
                    onSelected(item)
                },
                onClose = {
                    onClose(item)
                }
            )
            // if item is last, show add icon
            if (index == components.size - 1) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colors.onPrimary,
                    modifier = Modifier.size(addIconSize.dp).padding(horizontal = 8.dp)
                        .clickable {
                            onAdd()
                        }
                )
            }
        }
    }
}

@Composable
fun TabItem(
    tabWidthProvider: () -> Float,
    title: String,
    active: Boolean,
    onSelected: () -> Unit,
    onClose: () -> Unit
) {
    Row(
        Modifier.fillMaxHeight().width(tabWidthProvider().dp).background(
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
        Box(
            Modifier.fillMaxSize().weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                title,
                color = active.choose(MaterialTheme.colors.onBackground, MaterialTheme.colors.onPrimary),
                style = MaterialTheme.typography.caption,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            Modifier.fillMaxSize().weight(0.2f),
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
@Preview
fun TabItemPreview() {
    TabItem(
        tabWidthProvider = {
            168f
        },
        title = "测试Tab页",
        active = false,
        onSelected = {

        },
        onClose = {

        }
    )
}
package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.model.StructureNode
import org.spreadme.pdfgadgets.ui.common.VerticalScrollable
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelViewModel
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun StructureTree(
    structureRoot: StructureNode,
    sidePanelViewModel: SidePanelViewModel
) {
    VerticalScrollable(sidePanelViewModel) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            structureRoot.childs().forEach { StructureNodeView(it) }
        }
    }
}

@Composable
private fun StructureNodeView(node: StructureNode) {
    var expanded by remember { node.expanded }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .selectable(true) { expanded = !expanded }
            .padding(start = (24 * node.level).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        StructureNodePrefix(node.hasChild(), node.expanded)
        StructureNodeName(node)
    }

    AnimatedVisibility(
        expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column {
            node.childs().forEach {
                StructureNodeView(it)
            }
        }
    }

}

@Composable
private fun StructureNodePrefix(
    hasChild: Boolean,
    expanded: MutableState<Boolean>
) {
    if (hasChild) {
        Icon(
            expanded.value.choose(Icons.Default.ArrowDropDown, Icons.Default.ArrowRight),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.size(16.dp).clickable {
                expanded.value = !expanded.value
            }
        )
    } else {
        Box(modifier = Modifier.padding(start = 16.dp))
    }
}

@Composable
private fun StructureNodeName(node: StructureNode) {
    Icon(
        painter = painterResource(node.type.icon),
        contentDescription = "",
        tint = MaterialTheme.colors.onBackground,
        modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
    )
    Text(
        text = node.toString(),
        style = MaterialTheme.typography.caption,
        color = if (node.isCanParse()) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.onBackground
        },
        textAlign = TextAlign.Start,
        textDecoration = if (node.isCanParse()) {
            TextDecoration.Underline
        } else {
            null
        },
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.run {
            if (node.isCanParse()) {
                this.clickable(true) {

                }
            } else {
                this
            }
        }
    )
}
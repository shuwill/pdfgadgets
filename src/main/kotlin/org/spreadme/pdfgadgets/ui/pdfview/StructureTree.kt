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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.model.StructureNode
import org.spreadme.pdfgadgets.ui.common.VerticalScrollable
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.side.SideViewModel
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun StructureTree(
    structureRoot: StructureNode,
    sideViewModel: SideViewModel
) {
    VerticalScrollable(sideViewModel) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            structureRoot.childs().forEach { NodeView(it) }
        }
    }
}

@Composable
fun NodeView(node: StructureNode) {
    var expanded by rememberSaveable { node.expand }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .selectable(true) {
                expanded = !expanded
            }
            .padding(start = (24 * node.level).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        if (node.hasChild()) {
            Icon(
                expanded.choose(Icons.Default.ArrowDropDown, Icons.Default.ArrowRight),
                contentDescription = "",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier.size(16.dp).clickable {
                    expanded = !expanded
                }
            )
        } else {
            Box(modifier = Modifier.padding(start = 16.dp))
        }
        Icon(
            painter = painterResource(node.type.icon),
            contentDescription = "",
            tint = MaterialTheme.colors.onBackground,
            modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
        )
        Text(
            text = node.toString(),
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onBackground,
            textAlign = TextAlign.Start,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }

    AnimatedVisibility(
        expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column {
            node.childs().forEach {
                NodeView(it)
            }
        }
    }

}
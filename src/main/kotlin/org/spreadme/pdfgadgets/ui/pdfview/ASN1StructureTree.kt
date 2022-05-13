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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.model.ASN1Node
import org.spreadme.pdfgadgets.ui.common.VerticalScrollable
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.sidepanel.SidePanelUIState
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun ASN1StructureTree(
    asN1Node: ASN1Node,
    sidePanelUIState: SidePanelUIState,
) {
    VerticalScrollable(sidePanelUIState) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            asN1Node.childs().forEach { ASN1NodeView(it) }
        }
    }
}

@Composable
private fun ASN1NodeView(node: ASN1Node) {
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
        ASN1NodePrefix(node.type.hasChild, node.expanded)
        ASN1NodeName(node)
    }

    AnimatedVisibility(
        expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column {
            node.childs().forEach {
                ASN1NodeView(it)
            }
        }
    }

}

@Composable
private fun ASN1NodePrefix(
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
private fun ASN1NodeName(node: ASN1Node) {
    Text(
        text = node.toString(),
        style = MaterialTheme.typography.caption,
        color = MaterialTheme.colors.onBackground,
        textAlign = TextAlign.Start,
        softWrap = false,
        overflow = TextOverflow.Clip
    )
}
package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.spreadme.pdfgadgets.model.Position
import org.spreadme.pdfgadgets.model.Signature
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.VerticalScrollable
import org.spreadme.pdfgadgets.ui.common.clickable
import org.spreadme.pdfgadgets.ui.side.SideViewModel
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.utils.choose

@Composable
fun SignatureList(
    signatures: List<Signature>,
    sideViewModel: SideViewModel,
    onScroll: (postion: Position, scrollFinish: () -> Unit) -> Unit,
) {
    if (signatures.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(
                useResource(R.Drawables.signature_empty) {
                    loadSvgPainter(it, LocalDensity.current)
                },
                contentDescription = "",
                modifier = Modifier.padding(horizontal = 32.dp).widthIn(128.dp, 160.dp)
            )
        }
    } else {
        VerticalScrollable(sideViewModel) {
            Column(modifier = Modifier.fillMaxSize()) {
                signatures.forEach { signature ->
                    Column(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        val signatureResult = signature.signatureResult
                        val verify = signatureResult.verifySignature
                        var expanded by remember { signature.expand }
                        Row(
                            modifier = Modifier.fillMaxWidth().height(42.dp).selectable(true) {
                                expanded = !expanded
                            }.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val imageVector = expanded.choose(Icons.Default.ArrowDropDown, Icons.Default.ArrowDropDown)
                            Icon(
                                imageVector,
                                contentDescription = "",
                                tint = MaterialTheme.colors.onBackground,
                                modifier = Modifier.size(16.dp).clickable {
                                    expanded = !expanded
                                }
                            )

                            Icon(
                                painter = painterResource(R.Icons.signature),
                                contentDescription = "",
                                tint = verify.choose(LocalExtraColors.current.success, LocalExtraColors.current.error),
                                modifier = Modifier.padding(start = 8.dp).size(16.dp)
                            )

                            Text(
                                "由\"${signatureResult.signName}\"签名",
                                style = MaterialTheme.typography.caption,
                                color = MaterialTheme.colors.onBackground,
                                modifier = Modifier.padding(start = 8.dp),
                                textAlign = TextAlign.Center,
                                softWrap = false,
                                overflow = TextOverflow.Clip
                            )
                        }

                        SignatureListDetail(expanded, signature, onScroll)
                    }
                }
            }
        }
    }
}

@Composable
fun SignatureListDetail(
    expanded: Boolean,
    signature: Signature,
    onScroll: (postion: Position, scrollFinish: () -> Unit) -> Unit,
) {
    AnimatedVisibility(
        expanded,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            val showSignature = remember { mutableStateOf(false) }
            val currentSignInfo = remember { mutableStateOf<Signature?>(null) }
            signature.position?.let {
                Row(
                    modifier = Modifier.fillMaxWidth().height(24.dp).padding(start = 32.dp).clickable {
                        onScroll(it) {
                            it.selected.value = true
                        }
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "域: ${signature.fieldName}位于第${it.index}页",
                        style = MaterialTheme.typography.overline,
                        color = MaterialTheme.colors.onBackground,
                        softWrap = false,
                        overflow = TextOverflow.Clip
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().height(24.dp).padding(horizontal = 32.dp).clickable {
                    showSignature.value = true
                    currentSignInfo.value = signature
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "签名的详细信息...",
                    style = MaterialTheme.typography.overline,
                    color = MaterialTheme.colors.onBackground,
                )
            }
            currentSignInfo.value?.let {
                SignatureDetail(it, showSignature)
            }
        }
    }
}
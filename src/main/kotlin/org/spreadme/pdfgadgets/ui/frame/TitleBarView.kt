package org.spreadme.pdfgadgets.ui.frame

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.kanro.compose.jetbrains.expui.control.Icon
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.theme.IntUiThemes
import org.spreadme.pdfgadgets.ui.toolbars.ActionIcon

@Composable
fun DecoratedWindowScope.TitleBarView(applicationViewModel: ApplicationViewModel) {
    TitleBar(Modifier.newFullscreenControls(), gradientStartColor = applicationViewModel.projectColor) {
        Row(Modifier.align(Alignment.Start).padding(start = 12.dp)) {
            Dropdown(Modifier.height(30.dp), menuContent = {
                selectableItem(
                    selected = true,
                    onClick = {
                        applicationViewModel.newBlankTab()
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        ActionIcon(resource = R.Icons.pdf){}
                        Text("View PDF Structure")
                    }
                }
                selectableItem(
                    selected = true,
                    onClick = {
                        applicationViewModel.openASN1Parser()
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ActionIcon(resource = R.Icons.decode){}
                        Text("ASN1 View")
                    }
                }
            }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ActionIcon(resource = R.Icons.decode){}
                        applicationViewModel.currentComponent?.apply {
                            Text(this.name)
                        }
                    }
                }
            }
        }


        Row(Modifier.align(Alignment.End)) {
            Tooltip({
                when (applicationViewModel.theme) {
                    IntUiThemes.Light -> Text("Switch to light theme with light header")
                    IntUiThemes.Dark, IntUiThemes.System -> Text("Switch to light theme")
                }
            }) {
                IconButton({
                    applicationViewModel.isDark = !applicationViewModel.isDark
                    if(applicationViewModel.isDark) {
                        applicationViewModel.theme = IntUiThemes.Dark
                    } else {
                        applicationViewModel.theme = IntUiThemes.Light
                    }
                }, Modifier.size(40.dp).padding(5.dp)) {
                    when (applicationViewModel.theme) {
                        IntUiThemes.Light -> Icon(
                            "icons/lightTheme.svg",
                            "Themes"
                        )

                        IntUiThemes.Dark -> Icon(
                            "icons/darkTheme.svg",
                            "Themes"
                        )

                        IntUiThemes.System -> Icon(
                            "icons/systemTheme.svg",
                            "Themes"
                        )
                    }
                }
            }
        }
    }
}

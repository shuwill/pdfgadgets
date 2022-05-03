package org.spreadme.pdfgadgets.ui.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import java.awt.Cursor

@Composable
fun TextInputField(
    value: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle,
    singleLine: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onValueChange: (String) -> Unit,
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }
    val textFieldValue = textFieldValueState.copy(text = value)
    BasicTextField(
        value = textFieldValue,
        modifier = modifier,
        onValueChange = {
            textFieldValueState = it
            if (value != it.text) {
                onValueChange(it.text)
            }
        },
        textStyle = textStyle,
        singleLine = singleLine,
        enabled = enabled,
        readOnly = readOnly,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        decorationBox = @Composable { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    leadingIcon()
                }
                Box(Modifier.weight(1f)) {
                    innerTextField()
                }
                if (trailingIcon != null) {
                    trailingIcon()
                }
            }
        }
    )
}

@Composable
fun DropdownTextInputField(
    selectedText: String,
    suggestions: List<String>,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.caption,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    enabled: Boolean = true,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var textfieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded) {
        Icons.Filled.ArrowDropDown
    } else {
        Icons.Filled.ArrowDropUp
    }

    Column {
        val selectedTextState = mutableStateOf(selectedText)
        TextInputField(
            value = selectedTextState.value,
            onValueChange = { selectedTextState.value = it },
            textStyle = textStyle.copy(textAlign = TextAlign.Center),
            modifier = modifier.onGloballyPositioned { coordinates ->
                //This value is used to assign to the DropDown the same width
                textfieldSize = coordinates.size.toSize()
            },
            enabled = enabled,
            singleLine = true,
            readOnly = true,
            trailingIcon = {
                Icon(
                    icon,
                    contentDescription = "Expand Dropdown Menu",
                    tint = tint,
                    modifier = Modifier.padding(end = 8.dp).size(16.dp)
                        .pointerHoverIcon(PointerIcon(Cursor(Cursor.DEFAULT_CURSOR)))
                        .clickable(enabled) {
                            expanded = !expanded
                        }
                )
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textfieldSize.width.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(
                    onClick = {
                        selectedTextState.value = label
                        expanded = !expanded
                        onSelected(label)
                    },
                ) {
                    Text(text = label, style = textStyle)
                }
            }
        }
    }
}

@Composable
fun TextSearchInputField(
    keyword: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.caption,
    tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current),
    enabled: Boolean = true,
    trailing: (@Composable () -> Unit)? = null
) {
    TextInputField(
        value = keyword,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        singleLine = true,
        textStyle = textStyle,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "search text",
                tint = tint,
                modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
            )
        },
        trailingIcon = trailing
    )
}
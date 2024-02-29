package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryPopup(modifier: Modifier = Modifier, popupYoffset: Int = 0, onDismiss:() -> Unit) {
    Popup(
        alignment = Alignment.TopCenter,
        offset = IntOffset(x = 0, y = popupYoffset),
        properties = PopupProperties(focusable = true),
//expanded = queryMenuExpaneded.value,
        onDismissRequest = { onDismiss() }) {

        val setSelectedExpanded = remember { mutableStateOf(false) }
        val formatSelectedExpanded = remember { mutableStateOf(false) }
        val typeSelectedExpanded = remember { mutableStateOf(false) }
        val unqiueCheckbox = remember { mutableStateOf(false) }

        val (nameQuery, setNameQuery) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }


        val (cardTextQuery, setCardTextQuery) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }


        Column(
            modifier = modifier
                .padding(horizontal = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {
                OutlinedTextField(value = nameQuery,
                    onValueChange = { setNameQuery(it) },
                    label = { Text("Card Name:") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // keyboardController?.hide()
                            // focusManager.clearFocus()
                            //submitQuery(queryText.text)
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 2.dp),
                )
                OutlinedTextField(value = cardTextQuery,
                    onValueChange = { setCardTextQuery(it) },
                    label = { Text("Card Text:") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text,
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // keyboardController?.hide()
                            // focusManager.clearFocus()
                            //submitQuery(queryText.text)
                        }
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 2.dp)
                )

            }
            MultiChoiceSegmentedButtonRow(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                SegmentedButton(
                    checked = false,
                    onCheckedChange = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 4
                    )
                ) {
                    Text("Command")
                }
                SegmentedButton(
                    checked = false,
                    onCheckedChange = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 4
                    )
                ) {
                    Text("Force")
                }
                SegmentedButton(
                    checked = false,
                    onCheckedChange = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 2,
                        count = 4
                    )
                ) {
                    Text("Rogue")
                }
                SegmentedButton(
                    checked = false,
                    onCheckedChange = {},
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 3,
                        count = 4
                    )
                ) {
                    Text("General")
                }
            }


            Row(horizontalArrangement = Arrangement.SpaceAround) {
                NumericSelector(title = "Cost") {

                }
                NumericSelector(title = "Health") {
                    
                }
            }

            Row {
                Text("Set", modifier = Modifier.weight(1f))
                DropdownMenu(
                    expanded = setSelectedExpanded.value,
                    onDismissRequest = { setSelectedExpanded.value = false }) {

                }

                Text("Format", modifier = Modifier.weight(1f))
                DropdownMenu(
                    expanded = formatSelectedExpanded.value,
                    onDismissRequest = { formatSelectedExpanded.value = false }) {

                }

                Text("Type", modifier = Modifier.weight(1f))
                DropdownMenu(
                    expanded = typeSelectedExpanded.value,
                    onDismissRequest = { typeSelectedExpanded.value = false }) {

                }

                Text("Unique", modifier = Modifier.weight(1f))
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumericSelector(modifier: Modifier = Modifier, title: String, onSelect:() -> Unit) {
    val operator = remember { mutableStateOf('>') }
    val (num, setNum) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    Row(modifier = modifier) {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(selected = (operator.value == '>'),
                onClick = { operator.value = '>' },
                icon = {},
                shape = SegmentedButtonDefaults.itemShape(
                    index = 0,
                    count = 3
                )) {
                Text("<")
            }
            SegmentedButton(selected = (operator.value == '='),
                onClick = { operator.value = '=' },
                icon = {},
                shape = SegmentedButtonDefaults.itemShape(
                    index = 1,
                    count = 3
                )) {
                Text("=")
            }
            SegmentedButton(selected = (operator.value == '<'),
                onClick = { operator.value = '<' },
                icon = {},
                shape = SegmentedButtonDefaults.itemShape(
                    index = 2,
                    count = 3
                )) {
                Text("<")
            }
            OutlinedTextField(value = num,
                onValueChange = setNum,
                label = { Text(title) },
                modifier = Modifier.width(70.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
    }
}

@Preview(widthDp = 1000)
@Composable
fun QueryPopupPreview() {
    QueryPopup(modifier = Modifier.fillMaxWidth()) {

    }
}
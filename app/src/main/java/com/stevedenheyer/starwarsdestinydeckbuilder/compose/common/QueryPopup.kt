package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.NumericQuery
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.OperatorUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.dropDownInline
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.formatMap
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.getUniqueInline
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.typeMap
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.UiCardSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryPopup(modifier: Modifier = Modifier, sets: List<UiCardSet>, popupYoffset: Int = 0, onDismiss:() -> Unit, submitQuery:(QueryUi) -> Unit) {
    Popup(
        alignment = Alignment.TopCenter,
        offset = IntOffset(x = 0, y = popupYoffset),
        properties = PopupProperties(focusable = true),
//expanded = queryMenuExpaneded.value,
        onDismissRequest = { onDismiss() }) {
        val setsMap = sets.associate { Pair(it.code, it.name)  }

        val colors = remember { mutableStateListOf("red", "blue", "yellow", "gray") }

        val (nameQuery, setNameQuery) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        val (cardTextQuery, setCardTextQuery) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }

        val costNumbericQuery = remember { mutableStateOf(NumericQuery(operator = OperatorUi.LESS_THAN, number = 0)) }
        val healthNumbericQuery = remember { mutableStateOf(NumericQuery(operator = OperatorUi.LESS_THAN, number = 0)) }

        val setSelectedExpanded = remember { mutableStateOf(false) }
        val setSelected = remember { mutableStateOf<String?>(null) }

        val setFormatExpanded = remember { mutableStateOf(false) }
        val formatSelected = remember { mutableStateOf<String?>(null) }

        val setTypeExpanded = remember { mutableStateOf(false) }
        val typeSelected = remember { mutableStateOf<String?>(null) }

        val unqiueCheckbox = remember { mutableStateOf(false) }

        Column(
            modifier = modifier
                .padding(horizontal = 6.dp)
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Row(modifier = Modifier.padding(bottom = 8.dp)) {

                val textFieldColors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.outline,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

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
                    colors = textFieldColors,
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
                    colors = textFieldColors,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 2.dp)
                )

            }
            MultiChoiceSegmentedButtonRow(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                SegmentedButton(
                    checked = colors.contains("red"),
                    onCheckedChange = {
                                        if (colors.contains("red"))
                                            colors.remove("red")
                                            else
                                            colors.add("red") },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 4
                    )
                ) {
                    Text("Command", color = getColorFromString(s = "red"))
                }
                SegmentedButton(
                    checked = colors.contains("blue"),
                    onCheckedChange = {
                        if (colors.contains("blue"))
                            colors.remove("blue")
                        else
                            colors.add("blue")
                                      },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 4
                    )
                ) {
                    Text("Force", color = getColorFromString(s = "blue"))
                }
                SegmentedButton(
                    checked = colors.contains("yellow"),
                    onCheckedChange = {
                        if (colors.contains("yellow"))
                            colors.remove("yellow")
                        else
                            colors.add("yellow")
                                      },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 2,
                        count = 4
                    )
                ) {
                    Text("Rogue", color = getColorFromString(s = "yellow"))
                }
                SegmentedButton(
                    checked = colors.contains("gray"),
                    onCheckedChange = {
                        if (colors.contains("gray"))
                            colors.remove("gray")
                        else
                            colors.add("gray")
                                      },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 3,
                        count = 4
                    )
                ) {
                    Text("General", color = getColorFromString(s = "gray"))
                }
            }


            Row {
                NumericSelector(title = "Cost", modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)) {
                        costNumbericQuery.value = it
                }
                NumericSelector(title = "Health", modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(align = Alignment.CenterHorizontally)) {
                        healthNumbericQuery.value = it
                }
            }

            Row {
                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    .weight(1f)
                    .padding(horizontal = 1.dp)) {
                    Button(
                        onClick = { setSelectedExpanded.value = true },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(
                            buildAnnotatedString {
                                append(setsMap[setSelected.value] ?: "Set")
                                appendInlineContent("dropDownArrow", "[dropDownArrow]")
                            },
                            inlineContent = dropDownInline,
                        )
                    }
                    DropdownMenu(
                        expanded = setSelectedExpanded.value,
                        onDismissRequest = { setSelectedExpanded.value = false }) {

                        DropdownMenuItem(text = { Text("Any") },
                            onClick = {
                                setSelected.value = null
                                setSelectedExpanded.value = false
                            })
                        setsMap.forEach {
                            DropdownMenuItem(text = { Text(it.value) },
                                onClick = {
                                    setSelected.value = it.key
                                    setSelectedExpanded.value = false
                                })
                        }
                    }
                }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    .weight(1f)
                    .padding(horizontal = 1.dp)) {
                    Button(
                        onClick = { setFormatExpanded.value = true },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            buildAnnotatedString {
                                append(formatMap[formatSelected.value] ?: "Format")
                                appendInlineContent("dropDownArrow", "[dropDownArrow]")
                            },
                            inlineContent = dropDownInline,
                        )
                    }
                    DropdownMenu(
                        expanded = setFormatExpanded.value,
                        onDismissRequest = { setFormatExpanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Any") },
                            onClick = { formatSelected.value = null
                                setFormatExpanded.value = false})
                        formatMap.forEach {
                            DropdownMenuItem(
                                text = { Text(it.value) },
                                onClick = { formatSelected.value = it.key
                                    setFormatExpanded.value = false})
                        }
                    }
                }

                Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)
                    .weight(1f)
                    .padding(horizontal = 1.dp)) {
                    Button(
                        onClick = { setTypeExpanded.value = true },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            buildAnnotatedString {
                                append(typeMap[typeSelected.value] ?: "Type")
                                appendInlineContent("dropDownArrow", "[dropDownArrow]")
                            },
                            inlineContent = dropDownInline,
                        )
                    }
                    DropdownMenu(
                        expanded = setTypeExpanded.value,
                        onDismissRequest = { setTypeExpanded.value = false }) {
                        DropdownMenuItem(
                            text = { Text("Any") },
                            onClick = { typeSelected.value = null
                                setTypeExpanded.value = false})
                        typeMap.forEach {
                            DropdownMenuItem(
                                text = { Text(it.value) },
                                onClick = { typeSelected.value = it.key
                                    setTypeExpanded.value = false})
                        }
                    }
                }

                OutlinedButton(
                    onClick = { unqiueCheckbox.value = !unqiueCheckbox.value },
                    colors = if (!unqiueCheckbox.value)
                        ButtonColors(
                        containerColor = MaterialTheme.colorScheme.outline,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary,
                        disabledContentColor = MaterialTheme.colorScheme.onSecondary)
                    else
                        ButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                            disabledContainerColor = MaterialTheme.colorScheme.secondary,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondary),
                    shape = RoundedCornerShape(3.dp),
                    modifier = Modifier
                        .weight(0.7f)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                                appendInlineContent("unique", "[unique]")
                            append("Unique")
                        },
                        inlineContent = getUniqueInline(size = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer),
                    )
                }
            }
            Button(onClick = {
                if (
                    nameQuery.text.isBlank() &&
                    cardTextQuery.text.isBlank() &&
                    costNumbericQuery.value.number == 0 &&
                    healthNumbericQuery.value.number == 0 &&
                    colors.size == 4 &&
                    formatSelected.value.isNullOrBlank() &&
                    setSelected.value.isNullOrBlank() &&
                    typeSelected.value.isNullOrBlank() &&
                    unqiueCheckbox.value == false
                ) {
                    //Do nothing if not values set
                } else {
                    val query = QueryUi(
                        byCardName = nameQuery.text,
                        byCardText = cardTextQuery.text,
                        byColors = colors.toList(),
                        byCost = costNumbericQuery.value,
                        byHealth = healthNumbericQuery.value,
                        byFormat = formatSelected.value ?: "",
                        bySet = setSelected.value ?: "",
                        byType = typeSelected.value ?: "",
                        byUnique = unqiueCheckbox.value
                    )
                    submitQuery(query)
                    onDismiss()
                }
                             },
                modifier = Modifier.align(Alignment.End)) {
                Text("Search")

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumericSelector(modifier: Modifier = Modifier, title: String, onSelect:(NumericQuery) -> Unit) {
    val operator = remember { mutableStateOf(OperatorUi.LESS_THAN) }
    val (num, setNum) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    val textFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
        unfocusedContainerColor = MaterialTheme.colorScheme.outline,
        unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
    )

    Row(modifier = modifier) {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(selected = (operator.value == OperatorUi.LESS_THAN),
                onClick = { operator.value = OperatorUi.LESS_THAN
                            onSelect(NumericQuery(operator = OperatorUi.LESS_THAN,
                                number = try { num.text.toInt() } catch (e:NumberFormatException) { 0 }))},
                icon = {},
                modifier = Modifier.height(50.dp),
                shape = SegmentedButtonDefaults.itemShape(
                    index = 0,
                    count = 3
                )) {
                Text("<")
            }
            SegmentedButton(selected = (operator.value == OperatorUi.EQUALS),
                onClick = { operator.value = OperatorUi.EQUALS
                    onSelect(NumericQuery(operator = OperatorUi.EQUALS,
                        number = try { num.text.toInt() } catch (e:NumberFormatException) { 0 }))},
                icon = {},
                modifier = Modifier.height(50.dp),
                shape = SegmentedButtonDefaults.itemShape(
                    index = 1,
                    count = 3
                )) {
                Text("=")
            }
            SegmentedButton(selected = (operator.value == OperatorUi.MORE_THAN),
                onClick = { operator.value = OperatorUi.MORE_THAN
                    onSelect(NumericQuery(operator = OperatorUi.MORE_THAN,
                        number = try { num.text.toInt() } catch (e:NumberFormatException) { 0 }))},
                icon = {},
                modifier = Modifier.height(50.dp),
                shape = SegmentedButtonDefaults.itemShape(
                    index = 2,
                    count = 3
                )) {
                Text(">")
            }
            OutlinedTextField(value = num,
                onValueChange = { setNum(it)
                                onSelect(NumericQuery(operator = operator.value,
                                    number = try { it.text.toInt() } catch (e:NumberFormatException) { 0 })) },
                label = { Text(title) },
                colors = textFieldColors,
                modifier = Modifier
                    .width(90.dp)
                    .padding(start = 3.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

        }
    }
}

@Preview(widthDp = 1000)
@Composable
fun QueryPopupPreview() {
    QueryPopup(sets = emptyList(), modifier = Modifier.fillMaxWidth(), onDismiss = { }) {

    }
}
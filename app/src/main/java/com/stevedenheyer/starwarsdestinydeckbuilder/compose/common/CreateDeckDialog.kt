package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.affiliationMap
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.arrowInline
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.formatMap
import java.util.Date


@Composable
fun CreateDeckDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (deck: Deck) -> Unit,
    titleText: String,
) {

    var deckName by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    var formatDropDownExpanded by remember { mutableStateOf(false) }
    var formatSelection by remember { mutableStateOf("STD") }

    var affiliationDropDownExpanded by remember { mutableStateOf(false) }
    var affiliationSelection by remember { mutableStateOf("hero") }

    AlertDialog(title = { Text(titleText) },
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        titleContentColor = MaterialTheme.colorScheme.onSecondary,
        confirmButton = {
            val deck = Deck(
                name = deckName.text,
                creationDate = Date(),
                updateDate = Date(),
                formatCode = formatSelection,
                formatName = formatMap[formatSelection] ?: "",
                affiliationCode = affiliationSelection,
                affiliationName = affiliationMap[affiliationSelection] ?: "",
            )
            TextButton(onClick = { onConfirmation(deck) }) {
                Text("OK", color = MaterialTheme.colorScheme.onSecondary)
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.outline
                    )
                )
                Row {
                    Box(
                        modifier = Modifier
                            .wrapContentSize(align = Alignment.TopStart)
                            .weight(0.6f)
                    ) {
                        Button(
                            onClick = { formatDropDownExpanded = true },
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
                                    append(formatMap[formatSelection] ?: "Format")
                                    appendInlineContent("dropDownArrow", "[dropDownArrow]")
                                },
                                inlineContent = arrowInline,
                            )
                        }
                        DropdownMenu(
                            expanded = formatDropDownExpanded,
                            onDismissRequest = { formatDropDownExpanded = false }) {
                            formatMap.forEach { entry ->
                                DropdownMenuItem(
                                    text = { Text(entry.value) },
                                    onClick = {
                                        formatSelection = entry.key
                                        formatDropDownExpanded = false
                                    })
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .wrapContentSize(align = Alignment.TopStart)
                            .weight(0.4f)
                    ) {
                        Button(
                            onClick = { affiliationDropDownExpanded = true },
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
                                    append(affiliationMap[affiliationSelection] ?: "Affiliation")
                                    appendInlineContent("dropDownArrow", "[dropDownArrow]")
                                },
                                inlineContent = arrowInline,
                            )
                        }
                        DropdownMenu(
                            expanded = affiliationDropDownExpanded,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .wrapContentWidth(align = Alignment.CenterHorizontally),
                            onDismissRequest = { affiliationDropDownExpanded = false }) {
                            affiliationMap.forEach { entry ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            entry.value,
                                            color = MaterialTheme.colorScheme.onTertiaryContainer
                                        )
                                    },
                                    onClick = {
                                        affiliationSelection = entry.key
                                        affiliationDropDownExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}
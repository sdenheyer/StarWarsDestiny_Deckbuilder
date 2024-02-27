package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryTopBar(
    queryText: TextFieldValue,
    sortState: SortUi,
    setQueryText: (TextFieldValue) -> Unit,
    submitQuery: (String) -> Unit,
    openDrawer: () -> Unit,
    changeSortState: (SortState) -> Unit,
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val sortMenuExpanded = remember { mutableStateOf(false) }

    val queryMenuExpaneded = remember { mutableStateOf(false) }

    val (cardTextQuery, setCardTextQuery) = rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            TextField(
                value = queryText,
                onValueChange = { setQueryText(it) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Email,
                    autoCorrect = false
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        submitQuery(queryText.text)
                    }
                ),
                interactionSource = remember {
                    MutableInteractionSource()
                }.also { interactionSource ->
                    LaunchedEffect(key1 = interactionSource) {
                        interactionSource.interactions.collect {interaction ->
                            if (interaction is PressInteraction.Release) {
                                queryMenuExpaneded.value = true
                            }
                        }
                    }
                }

            )
            DropdownMenu(expanded = queryMenuExpaneded.value, onDismissRequest = { queryMenuExpaneded.value = false }) {
                TextField(value = cardTextQuery,
                    onValueChange = { setCardTextQuery(it) },
                    label = { Text("Card Text:") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Email,
                        autoCorrect = false
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            //submitQuery(queryText.text)
                        }
                    ),
                    modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally)
                    )

                MultiChoiceSegmentedButtonRow {
                    SegmentedButton(checked = false, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(
                        index = 0,
                        count = 4
                    )) {
                        Text("Command")
                    }
                    SegmentedButton(checked = false, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = 4
                    )) {
                        Text("Force")
                    }
                    SegmentedButton(checked = false, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(
                        index = 2,
                        count = 4
                    )) {
                        Text("Rogue")
                    }
                    SegmentedButton(checked = false, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(
                        index = 3,
                        count = 4
                    )) {
                        Text("General")
                    }
                }


            }
        },
        navigationIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                focusManager.clearFocus()
                openDrawer()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        },
        actions = {
            IconButton(onClick = { sortMenuExpanded.value = true }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_sort_24),
                    contentDescription = "Sort",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
            DropdownMenu(
                expanded = sortMenuExpanded.value,
                onDismissRequest = { sortMenuExpanded.value = false },
                offset = DpOffset(x = 0.dp, y = 8.dp),
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {

                val menuItemColors = MenuItemColors(
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    leadingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

                DropdownMenuItem(
                    text = { Text("Sort by Name") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.NAME) },
                    enabled = (sortState.sortState != SortState.NAME),
                    modifier = if (sortState.sortState == SortState.NAME) Modifier.background(color = MaterialTheme.colorScheme.tertiaryContainer) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Set") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SET) },
                    enabled = (sortState.sortState != SortState.SET),
                    modifier = if (sortState.sortState == SortState.SET) Modifier.background(color = MaterialTheme.colorScheme.tertiaryContainer) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Faction") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.FACTION) },
                    enabled = (sortState.sortState != SortState.FACTION),
                    modifier = if (sortState.sortState != SortState.FACTION) Modifier.background(color = MaterialTheme.colorScheme.tertiary) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Points/Cost") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.POINTS_COST) },
                    enabled = (sortState.sortState != SortState.POINTS_COST),
                    modifier = if (sortState.sortState != SortState.POINTS_COST) Modifier.background(color = MaterialTheme.colorScheme.tertiary) else Modifier
                )
                DropdownMenuItem(text = {
                    Text(buildAnnotatedString {
                        if (sortState.showHero)
                            append("Hide")
                        else
                            append("Show")
                        append(" Heroes")
                    })
                },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SHOW_HERO) })
                DropdownMenuItem(text = {
                    Text(buildAnnotatedString {
                        if (sortState.showVillain)
                            append("Hide")
                        else
                            append("Show")
                        append(" Villains")
                    })
                },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SHOW_VILLAIN) })
            }
        }
    )
}
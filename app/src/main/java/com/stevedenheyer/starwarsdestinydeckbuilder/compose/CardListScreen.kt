package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.menuUiState
import kotlinx.coroutines.launch
import java.util.Date

val formatMap =
    mapOf("STD" to "Standard", "TRI" to "Trilogy", "INF" to "Infinite", "ARHS" to "ARH Standard")
val affiliationMap = mapOf("hero" to "Hero", "villain" to "Villain", "neutral" to "Neutral")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    cardVM: CardViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val listUiState by cardVM.cardsFlow.collectAsStateWithLifecycle(
        UiState.noData(
            isLoading = true,
            errorMessage = null
        )
    )
    val menu by cardVM.menuItemsState.collectAsStateWithLifecycle(initialValue = menuUiState(isLoading = true, errorMessage = null, data = emptyList()))

    var queryText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val openCreateDeckDialog = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.secondary,
                drawerContentColor = MaterialTheme.colorScheme.onSecondary
            ) {
                val itemColors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = MaterialTheme.colorScheme.secondary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSecondary
                )
                NavigationDrawerItem(label = { Text("Create New Deck") },
                    selected = false,
                    colors = itemColors,
                    onClick = {
                        openCreateDeckDialog.value = true
                        scope.launch { drawerState.close() }
                    }
                )
                    LazyColumn {
                        items(items = menu.data, key = { it.code }) { item ->
                            NavigationDrawerItem(label = { Text(item.name) },
                                selected = false,
                                colors = itemColors,
                                onClick = {
                                    (cardVM::setCardSetSelection)(item.code)
                                    scope.launch { drawerState.close() }
                                })

                        }
                    }
            }
        })
    {

        when {
            openCreateDeckDialog.value -> {
                CreateDeckDialog(
                    onDismissRequest = { openCreateDeckDialog.value = false },
                    onConfirmation = { deck ->
                        if (deck.name.isNotBlank()) {
                            scope.launch {
                                try {
                                    (cardVM::createDeck)(deck)
                                    openCreateDeckDialog.value = false
                                } catch (e: SQLiteConstraintException) {
                                    openCreateDeckDialog.value = true
                                }
                            }
                        } else {
                            openCreateDeckDialog.value = false  //TODO:  User warnings
                        }

                    },
                    titleText = "Name your deck - must be unique"
                )
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        TextField(
                            value = queryText,
                            onValueChange = { queryText = it },
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
                                    (cardVM::findCard)(queryText.text)
                                }
                            ),
                            modifier = Modifier.clickable { keyboardController?.show() }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_menu_24),
                                contentDescription = "Menu",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    }
                )
            },
        ) { padding ->
            when (val state = listUiState) {
                is UiState.hasData -> TextList(
                    isCompactScreen,
                    cards = state.data,
                    modifier = modifier.padding(padding),
                    onCardClick
                )

                is UiState.noData -> {
                    if (state.isLoading) {
                        Text(
                            text = "Loading...", fontSize = 48.sp, modifier = modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    } else {
                        Text(
                            text = state.errorMessage ?: "",
                            fontSize = 48.sp,
                            modifier = modifier
                                .fillMaxSize()
                                .wrapContentSize(align = Alignment.Center)
                        )
                    }

                }
            }
        }
    }
}

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

    val inlineContent = mapOf("dropDownArrow" to InlineTextContent(
        placeholder = Placeholder(
            width = 14.sp,
            height = 24.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
        )
    })

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
                affiliationName = affiliationMap[affiliationSelection] ?: ""
            )
            TextButton(onClick = { onConfirmation(deck) }) {
                Text("OK", color = MaterialTheme.colorScheme.onSecondary)
            }
        },
        text = {
            Column {
                TextField(
                    value = deckName,
                    onValueChange = { deckName = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
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
                                inlineContent = inlineContent,
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
                                inlineContent = inlineContent,
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

@Preview
@Composable
fun Button() {
    val inlineContent = mapOf("dropDownArrow" to InlineTextContent(
        placeholder = Placeholder(
            width = 14.sp,
            height = 24.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    })

    Button(onClick = { }) {
        Text(buildAnnotatedString {
            append("Format")

            appendInlineContent("dropDownArrow", "[dropDownArrow]")
        }, inlineContent = inlineContent, modifier = Modifier)
    }
}

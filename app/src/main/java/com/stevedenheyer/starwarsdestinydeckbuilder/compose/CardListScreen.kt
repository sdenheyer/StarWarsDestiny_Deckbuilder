package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.CreateDeckDialog
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.MainTopBar
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.QueryDialog
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.SelectionDrawer
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SavedQueriesUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.dpToPx
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListTypeByQuery
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListTypeBySet
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListTypeCollection
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListTypeNone
import kotlinx.coroutines.launch
import java.lang.ClassCastException

@Composable
fun CardListScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    cardVM: ListViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
    onDeckSelect: (String) -> Unit,
) {

    val listUiState by cardVM.cardsFlow.collectAsStateWithLifecycle(
        UiState.noData(
            isLoading = false,
            errorMessage = null
        )
    )
    val setsUiState by cardVM.cardSetsFlow.collectAsStateWithLifecycle(
        initialValue = UiState.noData(
            isLoading = false,
            errorMessage = null
        )
    )

    val listType by cardVM.listTypeFlow.collectAsStateWithLifecycle()

    val decksUiState by cardVM.decksFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val sortState by cardVM.sortStateFlow.collectAsStateWithLifecycle(
        initialValue = SortUi(
            hideHero = true,
            hideVillain = true,
            sortState = SortState.SET
        )
    )

    val savedQueries by cardVM.savedQueries.collectAsStateWithLifecycle(
        initialValue = SavedQueriesUi(
            emptyList(),
            emptyList()
        )
    )

    val queryMenuExpaneded = remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val openCreateDeckDialog = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SelectionDrawer(
                decksUiState = decksUiState,
                setsUiState = setsUiState,
                createNewDeck = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    openCreateDeckDialog.value = true
                    scope.launch { drawerState.close() }
                },
                selectDeck = { deckName ->
                    onDeckSelect(deckName)
                    scope.launch { drawerState.close() }
                },
                selectSet = { setCode ->
                    (cardVM::setCardSetSelection)(setCode)
                    scope.launch { drawerState.close() }
                },
                selectCollection = {
                    (cardVM::showCollection)()
                    scope.launch { drawerState.close() }
                }
            )
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
                MainTopBar(
                    sortState = sortState,
                    openDrawer = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } },
                    changeSortState = { sortState -> (cardVM::setSort)(sortState) },
                    openQuery = { queryMenuExpaneded.value = true })
            },

            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }

        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    when (val type = listType) {
                        is ListTypeBySet -> Text(
                            "Set: ${type.setName}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(align = Alignment.CenterHorizontally)
                        )

                        is ListTypeCollection -> Text(
                            "My Collection",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(align = Alignment.CenterHorizontally)
                        )

                        is ListTypeByQuery -> Text(
                            "Query",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(align = Alignment.CenterHorizontally)
                        )

                        is ListTypeNone -> {}

                    }
                    if (listType is ListTypeNone) {

                    } else {
                        if (sortState.hideHero || sortState.hideVillain) {
                            Text(buildAnnotatedString {
                                if (sortState.hideHero && sortState.hideVillain) {
                                    append("Heros & Villains hidden")
                                } else {
                                    if (sortState.hideHero) {
                                        append("Heros hidden")
                                    } else {
                                        append("Villains hidden")
                                    }
                                }
                            },
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(align = Alignment.CenterHorizontally))
                        }

                        val numCards = try { (listUiState as UiState.hasData).data.size } catch(e: ClassCastException) { 0 }

                        Text("${numCards} cards",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(align = Alignment.CenterHorizontally))
                    }

                    when (val state = listUiState) {
                        is UiState.hasData -> {
                            if (listType is ListTypeCollection) {
                                CollectionList(
                                    isCompactScreen,
                                    cards = state.data,
                                    modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                    onItemClick = onCardClick,
                                    onRefreshSwipe = { (cardVM::showCollection)() }
                                )
                            } else {
                                CardList(
                                    isCompactScreen,
                                    cards = state.data,
                                    modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                    onItemClick = onCardClick,
                                    onRefreshSwipe = { (cardVM::refreshList)() }
                                )
                            }
                        }

                        is UiState.noData -> {
                                CardList(
                                    isCompactScreen,
                                    cards = emptyList(),
                                    modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer),
                                    onItemClick = onCardClick,
                                    onRefreshSwipe = { (cardVM::refreshList)() }
                                )

                            if (setsUiState.errorMessage != null) {
                                LaunchedEffect(snackbarHostState) {
                                    val result = snackbarHostState.showSnackbar(
                                        setsUiState.errorMessage!!,
                                        actionLabel = "Retry",
                                        duration = SnackbarDuration.Indefinite
                                    )
                                    if (result == SnackbarResult.ActionPerformed) {
                                        cardVM.refreshSets(true)
                                    }
                                }
                            } else {
                                if (listUiState.errorMessage != null) {
                                    LaunchedEffect(snackbarHostState) {
                                        snackbarHostState.showSnackbar(listUiState.errorMessage!!)
                                    }
                                }
                            }
                        }
                    }
                }

                if (queryMenuExpaneded.value) {
                    QueryDialog(isCompactScreen = isCompactScreen,
                        sets = if (setsUiState is UiState.noData) emptyList() else (setsUiState as UiState.hasData).data,
                        popupYoffset = padding.calculateTopPadding().dpToPx().toInt(),
                        savedQueries = savedQueries,
                        onDismiss = { queryMenuExpaneded.value = false },
                        submitQuery = { query -> (cardVM::findCards)(query) })
                }

                if (listUiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentSize(Alignment.Center)
                            .width(100.dp),
                        trackColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
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

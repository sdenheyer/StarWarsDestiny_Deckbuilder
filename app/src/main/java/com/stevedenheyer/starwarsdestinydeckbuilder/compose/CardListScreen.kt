package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.QueryTopBar
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.SelectionDrawer
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import kotlinx.coroutines.launch

@Composable
fun CardListScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    cardVM: CardViewModel = hiltViewModel(),
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

    val decksUiState by cardVM.decksFlow.collectAsStateWithLifecycle(initialValue = emptyList())

    val sortState by cardVM.sortStateFlow.collectAsStateWithLifecycle()

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
                createNewDeck = { focusManager.clearFocus()
                    keyboardController?.hide()
                   // setQueryText(TextFieldValue("", composition = null))
                    openCreateDeckDialog.value = true
                    scope.launch { drawerState.close() } },
                selectDeck = { deckName -> onDeckSelect(deckName)
                    scope.launch { drawerState.close() } },
                selectSet = { setCode -> (cardVM::setCardSetSelection)(setCode)
                    scope.launch { drawerState.close() }},
                selectCollection = { (cardVM::showCollection)()
                    scope.launch { drawerState.close() }}
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
                QueryTopBar(
                    sets = if (setsUiState is UiState.noData) emptyList() else (setsUiState as UiState.hasData).data,
                    sortState = sortState,
                    submitQuery = { (cardVM::findCards)(it) },
                    openDrawer = { scope.launch { drawerState.apply { if (isClosed) open() else close() } } },
                    changeSortState = { sortState -> (cardVM::setSort)(sortState) })
            },

            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }

        ) { padding ->
            Box {

                when (val state = listUiState) {
                    is UiState.hasData -> CardList(
                        isCompactScreen,
                        cards = state.data,
                        modifier = modifier.padding(padding),
                        onItemClick = onCardClick,
                        onRefreshSwipe = { (cardVM::refreshCardsBySet)(true) }
                    )

                    is UiState.noData -> {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .wrapContentSize(Alignment.Center)
                                    .width(100.dp),
                                trackColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

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

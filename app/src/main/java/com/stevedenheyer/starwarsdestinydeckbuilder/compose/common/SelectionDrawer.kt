package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.UiCardSet
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.UiDeck
import kotlinx.coroutines.launch

@Composable
fun SelectionDrawer(decksUiState: List<UiDeck>,
                    setsUiState: UiState<List<UiCardSet>>,
                    createNewDeck:() -> Unit,
                    selectDeck:(String) -> Unit,
                    selectSet:(String) -> Unit,
                    selectCollection:() -> Unit,) {

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.secondary,
        drawerContentColor = MaterialTheme.colorScheme.onSecondary
    ) {
        val itemColors = NavigationDrawerItemDefaults.colors(
            unselectedContainerColor = MaterialTheme.colorScheme.secondary,
            unselectedTextColor = MaterialTheme.colorScheme.onSecondary
        )
        NavigationDrawerItem(label = {
            Text(
                "Create New Deck", modifier = Modifier
                    .border(
                        Dp.Hairline,
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        },
            selected = false,
            colors = itemColors,
            onClick = {
                createNewDeck()
            }
        )

        NavigationDrawerItem(label = {
            Text(
                "Owned Cards", modifier = Modifier
                    .border(
                        Dp.Hairline,
                        color = MaterialTheme.colorScheme.onSecondary,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        },
            selected = false,
            colors = itemColors,
            onClick = {
                selectCollection()
            }
        )

        LazyColumn {
            if (decksUiState.isNotEmpty()) {
                item {
                    HorizontalDivider()
                    Text(
                        "Decks",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                    )
                }

                items(items = decksUiState, key = { it.name }) { deck ->
                    NavigationDrawerItem(label = { Text(deck.name) },
                        selected = false,
                        colors = itemColors,
                        onClick = {
                            selectDeck(deck.name)
                        })
                }
            }

            if (setsUiState is UiState.hasData) {
                val sets = (setsUiState as UiState.hasData<List<UiCardSet>>).data
                item {
                    HorizontalDivider()
                    Text(
                        "FFG Official",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp)
                    )
                }

                items(items = sets, key = { it.code }) { set ->
                    if (set.postition == 101) {
                        HorizontalDivider()
                        Text(
                            "A Renewed Hope",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp, top = 16.dp)
                        )
                    }
                    NavigationDrawerItem(label = { Text(set.name) },
                        selected = false,
                        colors = itemColors,
                        onClick = {
                            selectSet(set.code)
                        })

                }
            }

            if (setsUiState.isLoading) {
                item {
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
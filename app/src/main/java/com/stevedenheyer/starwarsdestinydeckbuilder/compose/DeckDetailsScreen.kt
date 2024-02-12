package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    isCompactScreen: Boolean,
    modifier: Modifier,
    deckVM: DeckViewModel = hiltViewModel()
    ) {

    val deckDetail by deckVM.deckDetail.collectAsStateWithLifecycle(UiState.noData(isLoading = false, errorMessage = null))


    Scaffold(
        topBar = {
            if (deckDetail is UiState.hasData)
               CenterAlignedTopAppBar( colors = TopAppBarDefaults.topAppBarColors(
                   containerColor = MaterialTheme.colorScheme.primaryContainer,
                   titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
               ),
                   title = { Text((deckDetail as UiState.hasData).data.name, style = MaterialTheme.typography.titleLarge) })
        }

    ) { padding ->
        if (deckDetail is UiState.hasData) {
            val deck = (deckDetail as UiState.hasData<DeckUi>).data

            val characters = deck.cards.filter { it.type == "Character" }
            val battlefields = deck.cards.filter { it.type == "Battlefield" }
            val plots = deck.cards.filter { it.type == "Plot" }
            val upgrades = deck.cards.filter { it.type == "Upgrade" }
            val downgrades = deck.cards.filter { it.type == "Downgrade" }
            val support = deck.cards.filter { it.type == "Support" }
            val events = deck.cards.filter { it.type == "Event" }

            LazyColumn(modifier = modifier.fillMaxSize().padding(padding)) {
                item {
                    Text("Character", style = MaterialTheme.typography.titleLarge)
                }

                items(items = characters, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Battlefield")
                }

                items(items = battlefields, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Plot")
                }

                items(items = plots, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Upgrade")
                }

                items(items = upgrades, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Downgrade")
                }

                items(items = downgrades, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Support")
                }

                items(items = support, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text("Event")
                }

                items(items = events, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }
            }
        }
    }
}
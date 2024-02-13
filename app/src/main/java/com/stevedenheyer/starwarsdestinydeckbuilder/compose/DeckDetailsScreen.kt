package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
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
            val charCardSize = characters.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val battlefield = deck.cards.filter { it.type == "Battlefield" }
            val plots = deck.cards.filter { it.type == "Plot" }
            val plotCardSize = plots.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val upgrades = deck.cards.filter { it.type == "Upgrade" }
            val upgradesCardSize = upgrades.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val upgradesDice = upgrades.map { if (it.diceRef.isNotEmpty()) it.quantity else {0} }.reduceOrNull { acc, dice -> acc + dice } ?: 0
            val downgrades = deck.cards.filter { it.type == "Downgrade" }
            val downgradesCardSize = downgrades.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val downgradesDice = downgrades.map { if (it.diceRef.isNotEmpty()) it.quantity else {0} }.reduceOrNull { acc, dice -> acc + dice } ?: 0
            val support = deck.cards.filter { it.type == "Support" }
            val supportCardSize = support.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val supportDice = support.map { if (it.diceRef.isNotEmpty()) it.quantity else {0} }.reduceOrNull { acc, dice -> acc + dice } ?: 0
            val events = deck.cards.filter { it.type == "Event" }
            val eventsCardSize = events.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
            val eventsDice = events.map { if (it.diceRef.isNotEmpty()) it.quantity else {0} }.reduceOrNull { acc, dice -> acc + dice } ?: 0

            Column(modifier = Modifier.padding(padding)) {
            OutlinedCard(onClick = {  },
                modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row {
                        Text(deck.affiliation)
                        Text(deck.format)
                    }
                    Row {
                        val plotPoints = plots.map { it.points.first ?: 0 }.reduceOrNull { acc, points -> acc + points} ?: 0
                        Text("Plots: ${plotPoints} points")
                        val charPoints = characters.map { (if (it.quantity == 2) it.points.second else it.points.first) ?: 0}.reduceOrNull { acc, points -> acc + points } ?: 0
                        val charDice = characters.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
                        Text("Characters: ${charPoints} points, ${charDice} dice")
                        val drawDeckSize = upgradesCardSize + downgradesCardSize + supportCardSize + eventsCardSize
                        val drawDeckDice = upgradesDice + downgradesDice + supportDice + eventsDice
                    }
                }
            }

            LazyColumn(modifier = modifier
            //    .fillMaxSize()
                ) {
                item {
                    Text(buildAnnotatedString {
                        append("Character")
                        //TODO:  Inline graphics
                        append("(${charCardSize})")
                    }, style = MaterialTheme.typography.titleLarge)
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
                    Text("Battlefield", style = MaterialTheme.typography.titleLarge)
                }

                items(items = battlefield, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { }
                    )
                }

                item {
                    Text(buildAnnotatedString {
                        append("Plots")
                        //TODO:  Inline graphics
                        append("(${plotCardSize})")
                    }, style = MaterialTheme.typography.titleLarge)
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
                    Text(buildAnnotatedString {
                        append("Upgrade")
                        //TODO:  Inline graphics
                        append("(${upgradesCardSize} ${upgradesDice})")
                    }, style = MaterialTheme.typography.titleLarge)
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
                    Text(buildAnnotatedString {
                        append("Downgrade")
                        //TODO:  Inline graphics
                        append("(${downgradesCardSize} ${downgradesDice})")
                    }, style = MaterialTheme.typography.titleLarge)
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
                    Text(buildAnnotatedString {
                        append("Support")
                        //TODO:  Inline graphics
                        append("(${supportCardSize} ${supportDice})")
                    }, style = MaterialTheme.typography.titleLarge)
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
                    Text(buildAnnotatedString {
                        append("Event")
                        //TODO:  Inline graphics
                        append("(${eventsCardSize} ${eventsDice})")
                    }, style = MaterialTheme.typography.titleLarge)
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
}
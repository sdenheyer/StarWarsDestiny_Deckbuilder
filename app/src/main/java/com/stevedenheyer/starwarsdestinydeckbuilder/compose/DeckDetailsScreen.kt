package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.getInlines
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckDetailUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckViewModel

@Composable
fun DeckDetailsScreen(
    isCompactScreen: Boolean,
    modifier: Modifier,
    deckVM: DeckViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
) {

    val deckDetail by deckVM.deckDetail.collectAsStateWithLifecycle(
        UiState.noData(
            isLoading = false,
            errorMessage = null
        )
    )

    Log.d("SWD", "Deckdetail: ${deckDetail}")

    when (val state = deckDetail) {
        is UiState.hasData -> DeckDetails(deck = state.data, isCompactScreen = isCompactScreen, onCardClick = onCardClick)

        is UiState.noData -> {}
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetails(modifier: Modifier = Modifier, isCompactScreen: Boolean, deck: DeckDetailUi, onCardClick: (String) -> Unit) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
                title = {
                    Text(
                        deck.name,
                        style = MaterialTheme.typography.headlineLarge
                    )
                })
        }

    ) { padding ->

       /* val characters = deck.chars
        val charCardSize =
            characters.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
        val battlefield = deck.battlefieldCard
        val plot = deck.plotCard
        val plotPoints = (if (deck.plotCard?.isElite == true) deck.plotCard.points.second else deck.plotCard?.points?.first) ?: 0
        val upgrades = deck.slots.filter { it.type == "Upgrade" }
        val upgradesCardSize =
            upgrades.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
        val upgradesDice = upgrades.map {
            if (it.diceRef.isNotEmpty()) it.quantity else {
                0
            }
        }.reduceOrNull { acc, dice -> acc + dice } ?: 0
        val downgrades = deck.slots.filter { it.type == "Downgrade" }
        val downgradesCardSize =
            downgrades.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
        val downgradesDice = downgrades.map {
            if (it.diceRef.isNotEmpty()) it.quantity else {
                0
            }
        }.reduceOrNull { acc, dice -> acc + dice } ?: 0
        val support = deck.slots.filter { it.type == "Support" }
        val supportCardSize =
            support.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
        val supportDice = support.map {
            if (it.diceRef.isNotEmpty()) it.quantity else {
                0
            }
        }.reduceOrNull { acc, dice -> acc + dice } ?: 0
        val events = deck.slots.filter { it.type == "Event" }
        val eventsCardSize =
            events.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0
        val eventsDice = events.map {
            if (it.diceRef.isNotEmpty()) it.quantity else {
                0
            }
        }.reduceOrNull { acc, dice -> acc + dice } ?: 0

        val charPoints = ((characters.map {
            (if (it.isElite) it.points.second else (it.points.first?.times(it.quantity))) ?: 0
        }.reduceOrNull { acc, points -> acc + points } ?: 0).plus(plotPoints))
        val charDice = characters.map { if (it.isElite) 2 else it.quantity }
            .reduceOrNull { acc, points -> acc + points } ?: 0
*/

        Column(modifier = Modifier.padding(padding)) {
            Card(
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row {
                        Text(
                            deck.affiliation,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            deck.format,
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Text(
                        "Characters: ${deck.charPoints} points, ${deck.charDice} dice",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    )
                    val drawDeckSize =
                        deck.upgrades.size + deck.downgrades.size + deck.support.size + deck.events.size
                    val drawDeckDice =
                        deck.upgrades.dice + deck.downgrades.dice + deck.support.dice + deck.events.dice
                    Text(
                        "Draw deck: ${drawDeckSize} cards, ${drawDeckDice} dice",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            LazyColumn(
                modifier = modifier
            ) {
                item {
                    Text(
                        buildAnnotatedString {
                            append("Character ")
                            append("(${deck.charCardSize}")
                            appendInlineContent("cards", "cards")
                            append("${deck.charDice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        inlineContent = getInlines()
                    )
                }

                items(items = deck.characters, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { onCardClick(card.code) }
                    )
                }

                item {
                    Text("Battlefield", style = MaterialTheme.typography.titleLarge)
                    if (deck.battlefield != null)
                        CardItem(
                            isScreenCompact = isCompactScreen,
                            modifier = Modifier,
                            card = deck.battlefield,
                            onItemClick = { onCardClick(deck.battlefield.code) }
                        )
                }


                item {
                    Text("Plot(${deck.plotPoints})",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    if (deck.plot != null)
                        CardItem(
                            isScreenCompact = isCompactScreen,
                            modifier = Modifier,
                            card = deck.plot,
                            onItemClick = { onCardClick(deck.plot.code) }
                        )
                }

                item {
                    Text(
                        buildAnnotatedString {
                            append("Upgrade")
                            //TODO:  Inline graphics
                            append("(${deck.upgrades.size}")
                            appendInlineContent("cards", "cards")
                            append("${deck.upgrades.dice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        inlineContent = getInlines()
                    )
                }

                items(items = deck.upgrades.cards, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { onCardClick(card.code) }
                    )
                }

                item {
                    Text(
                        buildAnnotatedString {
                            append("Downgrade")
                            append("(${deck.downgrades.size}")
                            appendInlineContent("cards", "cards")
                            append("${deck.downgrades.dice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        inlineContent = getInlines()
                    )
                }

                items(items = deck.downgrades.cards, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { onCardClick(card.code) }
                    )
                }

                item {
                    Text(
                        buildAnnotatedString {
                            append("Support")
                            append("(${deck.support.size}")
                            appendInlineContent("cards", "cards")
                            append("${deck.support.dice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        inlineContent = getInlines()
                    )
                }

                items(items = deck.support.cards, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { onCardClick(card.code) }
                    )
                }

                item {
                    Text(
                        buildAnnotatedString {
                            append("Event")
                            append("(${deck.events.size}")
                            appendInlineContent("cards", "cards")
                            append("${deck.events.dice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        inlineContent = getInlines()
                    )
                }

                items(items = deck.events.cards, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = { onCardClick(card.code) }
                    )
                }
            }
        }
    }
}

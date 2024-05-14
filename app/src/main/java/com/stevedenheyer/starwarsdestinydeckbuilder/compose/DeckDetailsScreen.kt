package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.getInlines
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckDetailUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.WarningsUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckDetailsScreen(
    isCompactScreen: Boolean,
    modifier: Modifier,
    deckVM: DeckViewModel = hiltViewModel(),
    onCardClick: (String) -> Unit,
    toDiceRoller: (String) -> Unit,
    navigateBack: () -> Unit,
) {

    val deckDetail by deckVM.deckDetail.collectAsStateWithLifecycle(
        UiState.noData(
            isLoading = false,
            errorMessage = null
        )
    )

    val warnings by deckVM.warnings.collectAsStateWithLifecycle(initialValue = WarningsUi.noWarnings)

    val snackbarHostState = remember { SnackbarHostState() }

    Log.d("SWD", "Deckdetail: ${deckDetail}")
    Scaffold(
        topBar = {
            val name = when (val state = deckDetail) {
                is UiState.hasData -> state.data.name
                is UiState.noData -> ""
            }
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
                title = {
                    Text(
                        name,
                        style = MaterialTheme.typography.headlineLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { toDiceRoller(name) },
                        enabled = name != ""
                        ) {
                        Icon(painter = painterResource(id = R.drawable.noun_cube_4025), contentDescription = "Dice Roller", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            )
        }

    ) { padding ->

        when (val state = deckDetail) {
            is UiState.hasData -> {
                if (deckDetail.errorMessage != null) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar(
                            deckDetail.errorMessage!!,
                            //  actionLabel = "Retry",
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }

                DeckDetails(
                    modifier = modifier.padding(padding),
                    deck = state.data,
                    warnings = warnings,
                    isCompactScreen = isCompactScreen,
                    onCardClick = onCardClick,
                )
            }

            is UiState.noData -> {
                if (deckDetail.errorMessage != null) {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar(
                            deckDetail.errorMessage!!,
                          //  actionLabel = "Retry",
                            duration = SnackbarDuration.Indefinite
                        )
                    }
                }
            }
        }

        if (deckDetail.isLoading) {
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


@Composable
fun DeckDetails(modifier: Modifier = Modifier, isCompactScreen: Boolean, deck: DeckDetailUi, warnings: WarningsUi, onCardClick: (String) -> Unit) {

        Column(modifier = modifier,
            verticalArrangement = Arrangement.Top) {
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
                    if (warnings != WarningsUi.noWarnings) {
                        if (warnings.bannedWarnings > 0)
                            Text(buildAnnotatedString {
                                append("${warnings.bannedWarnings} banned cards")
                                appendInlineContent("banned", "banned")
                            },
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),
                                inlineContent = getInlines()
                                )

                        if (warnings.uniqueWarnings > 0) {
                            Text("${warnings.uniqueWarnings} unique cards have copies",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),)
                        }

                        if (warnings.affiliationWarnings > 0) {
                            Text("${warnings.affiliationWarnings} cards with the wrong affiliation",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),)
                        }

                        if (warnings.factionWarnings > 0) {
                            Text("${warnings.factionWarnings} cards in draw deck don't have matching character faction",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),)
                        }

                        if (warnings.exceedingPointsWarning) {
                            Text("You have used too many points",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),)
                        }

                        if (warnings.exceedingDrawDeckWarning) {
                            Text("Your draw deck has too many cards",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.fillMaxWidth(),)
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.primaryContainer),
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        inlineContent = getInlines(MaterialTheme.colorScheme.onPrimaryContainer)
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
                    Text("Battlefield",
                    style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
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
                            append("(${deck.upgrades.size}")
                            appendInlineContent("cards", "cards")
                            append("${deck.upgrades.dice}")
                            appendInlineContent("dice", "dice")
                            append(")")
                        }, style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        inlineContent = getInlines(MaterialTheme.colorScheme.onPrimaryContainer)
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        inlineContent = getInlines(MaterialTheme.colorScheme.onPrimaryContainer)
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        inlineContent = getInlines(MaterialTheme.colorScheme.onPrimaryContainer)
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
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        inlineContent = getInlines(MaterialTheme.colorScheme.onPrimaryContainer)
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

package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetDeckWithCards
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeckDetailUi(
    val name: String,
    val format: String,
    val affiliation: String,
    val characters: List<CardUi>,
    val charCardSize: Int,
    val charPoints: Int,
    val charDice: Int,
    val battlefield: CardUi?,
    val plot: CardUi?,
    val plotPoints: Int,
    val upgrades: DeckSlotsUi,
    val downgrades: DeckSlotsUi,
    val support: DeckSlotsUi,
    val events: DeckSlotsUi,
) {
    companion object {
        fun toDeckDetailUi(deckUi: DeckUi) = DeckDetailUi(
            name = deckUi.name,
            format = deckUi.format,
            affiliation = deckUi.affiliation,
            characters = deckUi.chars,
            charCardSize = deckUi.chars.map { it.quantity }
                .reduceOrNull { acc, points -> acc + points } ?: 0,
            charPoints = (deckUi.chars.map {
                (if (it.isElite) it.points.second else (it.points.first?.times(it.quantity))) ?: 0
            }.reduceOrNull { acc, points -> acc + points } ?: 0),
            charDice = deckUi.chars.map { if (it.isElite) 2 else it.quantity }
                .reduceOrNull { acc, points -> acc + points } ?: 0,
            battlefield = deckUi.battlefieldCard,
            plot = deckUi.plotCard,
            plotPoints = (if (deckUi.plotCard?.isElite == true) deckUi.plotCard.points.second else deckUi.plotCard?.points?.first)
                ?: 0,
            upgrades = DeckSlotsUi.toDeckSlotsUi(deckUi.slots.filter { it.type == "Upgrade" }),
            downgrades = DeckSlotsUi.toDeckSlotsUi(deckUi.slots.filter { it.type == "Downgrade" }),
            support = DeckSlotsUi.toDeckSlotsUi(deckUi.slots.filter { it.type == "Support" }),
            events = DeckSlotsUi.toDeckSlotsUi(deckUi.slots.filter { it.type == "Event" }),
        )
    }

    data class DeckSlotsUi(
        val cards: List<CardUi>,
        val size: Int,
        val dice: Int,
    ) {
        companion object {
            fun toDeckSlotsUi(cardList: List<CardUi>) = DeckSlotsUi(
                cards = cardList,
                size = cardList.map { it.quantity }.reduceOrNull { acc, points -> acc + points } ?: 0,
                dice = cardList.map {
                    if (it.diceRef.isNotEmpty()) it.quantity else {
                        0
                    }
                }.reduceOrNull { acc, dice -> acc + dice } ?: 0
            )
        }
    }
}

data class WarningsUi(
    val bannedWarnings: Int,
    val affiliationWarnings: Int,
    val uniqueWarnings: Int,
    val factionWarnings: Int,
    val exceedingPointsWarning: Boolean,
    val exceedingDrawDeckWarning: Boolean,
) {
    companion object  {
        val noWarnings = WarningsUi(
            bannedWarnings = 0,
            affiliationWarnings = 0,
            uniqueWarnings = 0,
            factionWarnings = 0,
            exceedingPointsWarning = false,
            exceedingDrawDeckWarning = false,
        )

        fun toWarningsUi(deck: DeckDetailUi) = WarningsUi(
            bannedWarnings = (deck.characters.map { if (it.isBanned) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.downgrades.cards.map { if (it.isBanned) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.upgrades.cards.map { if (it.isBanned) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.support.cards.map { if (it.isBanned) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.events.cards.map { if (it.isBanned) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    if (deck.battlefield?.isBanned == true) 1 else 0 +
                    if (deck.plot?.isBanned == true) 1 else 0,
                affiliationWarnings = (deck.characters.map { if (it.affiliationMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                        (deck.downgrades.cards.map { if (it.affiliationMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                        (deck.upgrades.cards.map { if (it.affiliationMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                        (deck.support.cards.map { if (it.affiliationMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                        (deck.events.cards.map { if (it.affiliationMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                        if (deck.battlefield?.affiliationMismatchWarning == true) 1 else 0 +
                                if (deck.plot?.affiliationMismatchWarning == true) 1 else 0,
            uniqueWarnings = (deck.characters.map { if (it.uniqueWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.downgrades.cards.map { if (it.uniqueWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.upgrades.cards.map { if (it.uniqueWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.support.cards.map { if (it.uniqueWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.events.cards.map { if (it.uniqueWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    if (deck.battlefield?.uniqueWarning == true) 1 else 0 +
                            if (deck.plot?.uniqueWarning == true) 1 else 0,
            factionWarnings = (deck.characters.map { if (it.factionMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.downgrades.cards.map { if (it.factionMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.upgrades.cards.map { if (it.factionMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.support.cards.map { if (it.factionMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    (deck.events.cards.map { if (it.factionMismatchWarning) 1 else 0 }.reduceOrNull { acc, b -> acc + b } ?: 0) +
                    if (deck.battlefield?.factionMismatchWarning == true) 1 else 0 +
                            if (deck.plot?.factionMismatchWarning == true) 1 else 0,
            exceedingPointsWarning = (deck.plotPoints + deck.charPoints > 30),
            exceedingDrawDeckWarning = (deck.upgrades.size + deck.downgrades.size + deck.support.size + deck.events.size > 30)
        )
    }
}

@HiltViewModel
class DeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeckWithCards: GetDeckWithCards,
    private val repo: CardRepositoryImpl,
) : ViewModel() {

    private val deckCode: String = checkNotNull(savedStateHandle["name"])

    val deckDetail = getDeckWithCards(deckCode).map { deckState ->
        when (deckState) {
            is UiState.NoData -> UiState.NoData(
                isLoading = deckState.isLoading,
                errorMessage = deckState.errorMessage
            )

            is UiState.HasData -> {
                val deck = deckState.data
                val factions = deck.chars.map { it.faction }
                val newDeck = deck.copy(
                    chars = deck.chars.map { card ->
                        val newCard =
                            card.copy(
                                affiliationMismatchWarning = !(card.affiliation == deck.affiliation || card.affiliation == "Neutral"),
                                uniqueWarning = if (!card.isUnique) false else {
                                    deck.chars.filter { it.name == card.name }.size > 1
                                }
                            )
                        newCard
                    },
                    battlefieldCard = deck.battlefieldCard?.copy(
                        affiliationMismatchWarning = !(deck.battlefieldCard.affiliation == deck.affiliation || deck.battlefieldCard.affiliation == "Neutral"),
                        factionMismatchWarning = !(deck.battlefieldCard.faction in factions || deck.battlefieldCard.faction == "General")
                    ),
                    plotCard = deck.plotCard?.copy(
                        affiliationMismatchWarning = !(deck.plotCard.affiliation == deck.affiliation || deck.plotCard.affiliation == "Neutral"),
                        factionMismatchWarning = !(deck.plotCard.faction in factions || deck.plotCard.faction == "General")
                    ),
                    slots = deck.slots.map { card ->
                        val newCard =
                            card.copy(
                                affiliationMismatchWarning = !(card.affiliation == deck.affiliation || card.affiliation == "Neutral"),
                                factionMismatchWarning = !(card.faction in factions || card.faction == "General"),
                                uniqueWarning = if (!card.isUnique) false else {
                                    deck.slots.filter { it.name == card.name }.size > 1
                                }
                            )
                        newCard
                    }
                )
                UiState.HasData(
                    isLoading = deckState.isLoading,
                    errorMessage = deckState.errorMessage,
                    data = DeckDetailUi.toDeckDetailUi(newDeck)
                )
            }
        }

    }

    val warnings = deckDetail.map { deckState ->
        when (deckState) {
            is UiState.NoData -> WarningsUi.noWarnings

            is UiState.HasData -> {
                val deck = deckState.data   
                WarningsUi.toWarningsUi(deck)
            }
        }
    }

    fun deleteDeck() = viewModelScope.launch(Dispatchers.IO) {
        val deck = repo.getDeck(deckCode)
        repo.deleteDeck(deck)
    }

    fun getDeckName() = deckCode
}
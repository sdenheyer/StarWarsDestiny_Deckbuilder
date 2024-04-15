package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetDeckWithCards
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
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

@HiltViewModel
class DeckViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeckWithCards: GetDeckWithCards,
) : ViewModel() {

    val deckCode: String = checkNotNull(savedStateHandle.get("name"))

    val deckDetail = getDeckWithCards(deckCode).map { deckState ->
        when (val state = deckState) {
            is UiState.noData -> UiState.noData(
                isLoading = state.isLoading,
                errorMessage = state.errorMessage
            )

            is UiState.hasData -> {
                val deck = state.data
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
                UiState.hasData(
                    isLoading = state.isLoading,
                    errorMessage = state.errorMessage,
                    data = DeckDetailUi.toDeckDetailUi(newDeck)
                )
            }
        }

    }

}
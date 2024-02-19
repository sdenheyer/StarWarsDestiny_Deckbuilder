package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asIntPair
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException
import javax.inject.Inject

class GetDeckWithCards @Inject constructor(private val cardRepo: CardRepositoryImpl, val getCards: GetCardFromCode) {
    operator fun invoke(deckName: String, forceRemoteUpdate: Boolean = false) = flow {
        val deck = cardRepo.getDeck(deckName)

        cardRepo.getCardFormats(forceRemoteUpdate).collect { response ->
            when (response.status) {
                Resource.Status.LOADING -> { emit(UiState.hasData(isLoading = true, errorMessage = null, data = deck.toDeckUi())) }
                Resource.Status.ERROR -> { emit(UiState.hasData(isLoading = false, errorMessage = response.message, data = deck.toDeckUi())) }
                Resource.Status.SUCCESS -> {

                        val format: CardFormat = try{
                            checkNotNull(response.data?.cardFormats?.find { it.gameTypeName == deck.formatName })
                    } catch (e: IllegalStateException) {
                        CardFormat(gameTypeCode = deck.formatCode, gameTypeName = deck.formatName, balance = emptyMap(), includedSets = emptyList(), banned = emptyList(), restricted = emptyList(), restrictedPairs = emptyMap())
                    }

                    var deckUi = deck.toDeckUi()
                    val charCodes = deck.characters.map { char -> char.cardOrCode }.toTypedArray()
                    Log.d("SWD", "Charcodes: ${charCodes.size}")
                    val chars = getCardsUi(deck, format, *charCodes)

                    when (chars) {
                        is UiState.noData -> emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                        is UiState.hasData -> deckUi = deckUi.copy(chars = chars.data)
                    }
                    Log.d("SWD", "Deck chars: ${deckUi.chars.size}")

                    val slotCodes = deck.slots.map { slot -> slot.cardOrCode }.toTypedArray()
                    Log.d("SWD", "Slotcodes: ${slotCodes.size}")
                    val slots = getCardsUi(deck, format, *slotCodes)

                    when (slots) {
                        is UiState.noData -> emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                        is UiState.hasData -> deckUi = deckUi.copy(cards = slots.data)
                    }
                    Log.d("SWD", "Deck slots: ${deckUi.cards.size}")

                    if (deck.battlefieldCardCode != null) {
                        val battlefield = getCardsUi(deck, format, deck.battlefieldCardCode)
                        when (battlefield) {
                            is UiState.noData -> emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                            is UiState.hasData -> deckUi = deckUi.copy(battlefieldCard = battlefield.data.first())
                        }
                    }

                    if (deck.plotCardCode != null) {
                        val plot = getCardsUi(deck, format, deck.plotCardCode)
                        when (plot) {
                            is UiState.noData -> emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                            is UiState.hasData -> deckUi = deckUi.copy(battlefieldCard = plot.data.first())
                        }
                    }
                    Log.d("SWD", "Emitting...")
                    emit(UiState.hasData(isLoading = false, errorMessage = null, data = deckUi))
                }
            }
        }
    }

    private suspend fun getCardsUi(deck: Deck, format: CardFormat, vararg charCodes:CardOrCode, ): UiState<List<CardUi>> {
        return UiState.hasData(isLoading = false, errorMessage = null, data = getCards(*charCodes).first().map { card ->
            when (card) {
                is CardOrCode.hasCode -> return UiState.noData(isLoading = false, errorMessage = card.msg)
                is CardOrCode.hasCard -> { var uiCard = card.card.toCardUi()
                    var deckCard = if (card.card.typeName == "Character")
                        try {
                        checkNotNull(deck.characters.find { it.cardOrCode.fetchCode() == uiCard.code })
                    }catch (e: IllegalStateException) {
                        return UiState.hasData(isLoading = false, errorMessage = null, data = emptyList())
                    } else {
                        try {
                            checkNotNull(deck.slots.find { it.cardOrCode.fetchCode() == uiCard.code })
                        }catch (e: IllegalStateException) {
                            return UiState.hasData(isLoading = false, errorMessage = null, data = emptyList())
                        }
                    }
                    val isBanned = getIsBanned(card.card, format)
                    val balance = getBalance(card.card, format)
                    if (isBanned == null) {
                        return UiState.hasData(isLoading = false, errorMessage = "Unknown error", data = listOf(uiCard))
                    }
                    uiCard = uiCard.copy(quantity = deckCard.quantity,
                        isElite = deckCard.isElite,
                        isBanned = isBanned,
                        points = balance)
                    uiCard
                }
            }
        })
    }

    private suspend fun getIsBanned(card: Card, format: CardFormat): Boolean? {
        val reprints = getCards(*card.reprints.toTypedArray()).first().map {
            when (it) {
                is CardOrCode.hasCard -> it.card
                is CardOrCode.hasCode -> return null
            }
        }


        if (card.setCode in format.includedSets ||
            reprints.any { it.setCode in format.includedSets })
        {
            if (card.setCode in format.banned)
                return true
        } else {
            return true
        }
        return false
    }

    private fun getBalance(card: Card, format: CardFormat):Pair<Int?, Int?> {
        val balance = format.balance.get(card.setCode).asIntPair()
        if (balance.first != null) {
            return balance
        } else {
            return card.points
        }

    }
}
package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.DeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asIntPair
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.lang.IllegalStateException
import javax.inject.Inject

class GetDeckWithCards @Inject constructor(private val cardRepo: CardRepositoryImpl, val getCards: GetCardFromCode) {
    operator fun invoke(deckName: String, forceRemoteUpdate: Boolean = false) = flow {
        var deck = cardRepo.getDeck(deckName)

        cardRepo.getCardFormats(forceRemoteUpdate).collect { response ->
            when (response.status) {
                Resource.Status.LOADING -> { emit(UiState.hasData(isLoading = true, errorMessage = null, data = deck.toDeckUi())) }

                Resource.Status.ERROR -> { emit(
                    UiState.noData(
                        isLoading = false,
                        errorMessage = response.message
                    )
                )
                return@collect
                }

                Resource.Status.SUCCESS -> {
                        val format: CardFormat = try{
                            checkNotNull(response.data?.cardFormats?.find { it.gameTypeName == deck.formatName })
                    } catch (e: IllegalStateException) {
                        CardFormat(gameTypeCode = deck.formatCode, gameTypeName = deck.formatName, balance = emptyMap(), includedSets = emptyList(), banned = emptyList(), restricted = emptyList(), restrictedPairs = emptyMap())
                    }

                   // var deckUi = deck.toDeckUi()
                    val charCodes = deck.characters.map { char -> char.cardOrCode }.toTypedArray()
                    Log.d("SWD", "Charcodes: ${charCodes.size}")
                    val chars = getCardsUi(*charCodes)

                    when (val state = chars) {
                        is UiState.noData -> { emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                                            return@collect }
                        is UiState.hasData -> { val charCards = deck.characters.map { charCard ->
                                val card = checkNotNull(state.data.find { it.fetchCode() == charCard.cardOrCode.fetchCode() })
                                charCard.copy(cardOrCode = card)
                            }
                        deck = deck.copy(characters = charCards)
                        }
                    }
                    Log.d("SWD", "Deck chars: ${deck.characters.size}")

                    val slotCodes = deck.slots.map { slot -> slot.cardOrCode }.toTypedArray()
                    Log.d("SWD", "Slotcodes: ${slotCodes.size}")
                    val slots = getCardsUi(*slotCodes)

                    when (val state = slots) {
                        is UiState.noData -> { emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                                            return@collect}
                        is UiState.hasData -> { val slotCards = deck.slots.map { slotCard ->
                            val card = checkNotNull(state.data.find { it.fetchCode() == slotCard.cardOrCode.fetchCode() })
                            slotCard.copy(cardOrCode = card)
                        }
                            deck = deck.copy(slots = slotCards)
                        }
                    }
                    Log.d("SWD", "Deck slots: ${deck.slots.size}")

                    if (deck.battlefieldCardCode != null) {
                        val battlefield = getCardsUi(deck.battlefieldCardCode!!)
                        when (battlefield) {
                            is UiState.noData -> emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                            is UiState.hasData -> deck = deck.copy(battlefieldCardCode = battlefield.data.first())
                        }
                    }

                    if (deck.plotCardCode != null) {
                        val plot = getCardsUi(deck.plotCardCode!!)
                        when (plot) {
                            is UiState.noData -> { emit(UiState.hasData(isLoading = false, errorMessage = chars.errorMessage, data = deck.toDeckUi()))
                                return@collect
                            }
                            is UiState.hasData -> deck = deck.copy(plotCardCode = plot.data.first())
                        }
                    }
                    Log.d("SWD", "Emitting...")

                    val charsUi = deck.characters.map {  (it.cardOrCode as CardOrCode.hasCard).card.toCardUi(format, it.quantity, it.isElite) }
                    val slotsUi = deck.slots.map { (it.cardOrCode as CardOrCode.hasCard).card.toCardUi(format, it.quantity) }
                    val battlefieldUi = try { (deck.battlefieldCardCode as CardOrCode.hasCard).card.toCardUi(format) } catch (e:NullPointerException) { null }
                    val plotUi = try { (deck.plotCardCode as CardOrCode.hasCard).card.toCardUi(format, isElite = deck.isPlotElite) } catch (e:NullPointerException) { null }

                    val deckUi = deck.toDeckUi().copy(chars = charsUi, slots = slotsUi, battlefieldCard = battlefieldUi, plotCard = plotUi)

                    emit(UiState.hasData(isLoading = false, errorMessage = null, data = deckUi))
                }
            }
        }
    }

    private suspend fun getCardsUi(vararg charCodes:CardOrCode): UiState<List<CardOrCode>> {

        val cards = getCards(false, *charCodes).first().map { card ->
            when (card) {
                is CardOrCode.hasCode -> return UiState.noData(isLoading = false, errorMessage = card.msg)
                is CardOrCode.hasCard -> card
                }
            }
        return UiState.hasData(isLoading = false, errorMessage = null, data = cards)
        }

    private suspend inline fun Card.toCardUi(format: CardFormat, quantity: Int = 1, isElite: Boolean = false):CardUi {
        val isBanned = getIsBanned(this, format)
        val balance = getBalance(this, format)
        return this.toCardUi().copy(isBanned = isBanned, isElite = isElite, points = balance, quantity = quantity)
    }

    private suspend inline fun getIsBanned(card: Card, format: CardFormat): Boolean {
        val reprints = getCards(false, *card.reprints.toTypedArray()).first().map {
            when (it) {
                is CardOrCode.hasCard -> it.card
                is CardOrCode.hasCode -> return false
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
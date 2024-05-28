package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.di.IoDispatcher
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asIntPair
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import java.lang.IllegalStateException
import javax.inject.Inject

class GetDeckWithCards @Inject constructor(
    private val cardRepo: CardRepository,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(deckName: String, forceRemoteUpdate: Boolean = false) = flow {
        val deck = cardRepo.getDeck(deckName)
        var uiDeck = deck.toDeckUi()

        cardRepo.getCardFormats(forceRemoteUpdate).collect { response ->
            when (response.status) {
                Resource.Status.LOADING -> {
                    emit(
                        UiState.HasData(
                            isLoading = true,
                            errorMessage = null,
                            data = deck.toDeckUi()
                        )
                    )
                }

                Resource.Status.ERROR -> {
                    emit(
                        UiState.NoData(
                            isLoading = false,
                            errorMessage = response.message
                        )
                    )
                    return@collect
                }

                Resource.Status.SUCCESS -> {

                    val format: CardFormat = try {
                        checkNotNull(response.data?.cardFormats?.find { it.gameTypeName == deck.formatName })
                    } catch (e: IllegalStateException) {
                        CardFormat(
                            gameTypeCode = deck.formatCode,
                            gameTypeName = deck.formatName,
                            balance = emptyMap(),
                            includedSets = emptyList(),
                            banned = emptyList(),
                            restricted = emptyList(),
                            restrictedPairs = emptyMap()
                        )
                    }

                    val slotJob = coroutineScope.async(dispatcher) {
                        val codes = deck.slots.map { slot -> slot.cardOrCode }.toTypedArray()
                        val resource = cardRepo.getCardsByCodes(*codes).last()
                        val cards =
                            resource.data
                        val slotsMap =
                            deck.slots.associateBy { it.cardOrCode.fetchCode() }.toMutableMap()
                        cards?.forEach {
                            slotsMap[it.fetchCode()] =
                                slotsMap[it.fetchCode()]!!.copy(cardOrCode = it)
                        }
                        val uiCards = slotsMap.values.map {
                            (it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                format,
                                it.quantity,
                                false
                            )
                        }
                        Resource(resource.status, uiCards, resource.isFromDB, resource.message)
                    }

                    val charJob = coroutineScope.async(dispatcher) {
                        val codes = deck.characters.map { slot -> slot.cardOrCode }.toTypedArray()
                        val resource = cardRepo.getCardsByCodes(*codes).last()
                        val cards =
                            resource.data
                        val charsMap =
                            deck.characters.associateBy { it.cardOrCode.fetchCode() }.toMutableMap()
                        cards?.forEach {
                            charsMap[it.fetchCode()] =
                                charsMap[it.fetchCode()]!!.copy(cardOrCode = it)
                        }
                        val uiCards = charsMap.values.map {
                            (it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                format,
                                it.quantity,
                                it.isElite
                            )
                        }
                        Resource(resource.status, uiCards, resource.isFromDB, resource.message)
                    }

                    val battlefieldJob = if (deck.battlefieldCardCode == null) {
                        null
                    } else
                        coroutineScope.async(dispatcher) {
                            val code = deck.battlefieldCardCode
                            val resource = cardRepo.getCardByCode(code.fetchCode(), false)
                                .first { it.status != Resource.Status.LOADING }
                            val uiCard = resource.data?.toCardUi()
                            Resource(resource.status, uiCard, resource.isFromDB, resource.message)
                        }

                    val plotJob = if (deck.plotCardCode == null) {
                        null
                    } else
                        coroutineScope.async(dispatcher) {
                            val code = deck.plotCardCode
                            val resource = cardRepo.getCardByCode(code.fetchCode(), false)
                                .first { it.status != Resource.Status.LOADING }
                            val uiCard = resource.data?.toCardUi()
                            Resource(resource.status, uiCard, resource.isFromDB, resource.message)
                        }

                    if (battlefieldJob != null) {
                        val battlefield = battlefieldJob.await()
                        when (battlefield.status) {
                            Resource.Status.ERROR -> {
                                emit(
                                    UiState.HasData(
                                        isLoading = false,
                                        errorMessage = battlefield.message,
                                        data = uiDeck
                                    )
                                )
                                return@collect
                            }

                            else -> {
                                uiDeck = uiDeck.copy(battlefieldCard = battlefield.data)
                                emit(
                                    UiState.HasData(
                                        isLoading = true,
                                        errorMessage = battlefield.message,
                                        data = uiDeck
                                    )
                                )
                            }
                        }
                    }

                    if (plotJob != null) {
                        val plot = plotJob.await()
                        when (plot.status) {
                            Resource.Status.ERROR -> {
                                emit(
                                    UiState.HasData(
                                        isLoading = false,
                                        errorMessage = plot.message,
                                        data = uiDeck
                                    )
                                )
                                return@collect
                            }

                            else -> {
                                uiDeck = uiDeck.copy(plotCard = plot.data)
                                emit(
                                    UiState.HasData(
                                        isLoading = true,
                                        errorMessage = plot.message,
                                        data = uiDeck
                                    )
                                )
                            }
                        }
                    }

                    val chars = charJob.await()
                    when (chars.status) {
                        Resource.Status.ERROR -> {
                            emit(
                                UiState.HasData(
                                    isLoading = false,
                                    errorMessage = chars.message,
                                    data = uiDeck
                                )
                            )
                            return@collect
                        }

                        else -> {
                            uiDeck = uiDeck.copy(chars = chars.data ?: emptyList())
                            emit(
                                UiState.HasData(
                                    isLoading = true,
                                    errorMessage = chars.message,
                                    data = uiDeck
                                )
                            )
                        }
                    }

                    val slots = slotJob.await()
                    when (slots.status) {
                        Resource.Status.ERROR -> {
                            emit(
                                UiState.HasData(
                                    isLoading = false,
                                    errorMessage = chars.message,
                                    data = uiDeck
                                )
                            )
                            return@collect
                        }

                        else -> {
                            uiDeck = uiDeck.copy(slots = slots.data ?: emptyList())
                            emit(
                                UiState.HasData(
                                    isLoading = false,
                                    errorMessage = chars.message,
                                    data = uiDeck
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend inline fun Card.toUi(
        format: CardFormat,
        quantity: Int = 1,
        isElite: Boolean = false
    ): CardUi {
        val isBanned = getIsBanned(this, format)
        val balance = getBalance(this, format)
        return this.toCardUi()
            .copy(isBanned = isBanned, isElite = isElite, points = balance, quantity = quantity)
    }

    private suspend inline fun getIsBanned(card: Card, format: CardFormat): Boolean {
        val resource = cardRepo.getCardsByCodes(*card.reprints.toTypedArray()).last()
        when (resource.status) {
            Resource.Status.ERROR -> return false
            else -> {
                val reprints = resource.data?.map { (it as CardOrCode.HasCard).card } ?: emptyList()
                return if (
                    card.setCode in format.includedSets ||
                    reprints.any { it.setCode in format.includedSets }
                ) {
                    card.setCode in format.banned
                } else {
                    true
                }
            }
        }
    }

    private fun getBalance(card: Card, format: CardFormat): Pair<Int?, Int?> {
        val balance = format.balance[card.setCode].asIntPair()
        return if (balance.first != null) {
            balance
        } else {
            card.points
        }

    }
}
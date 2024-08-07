package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import android.util.Log
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.CardSetIcon
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.lang.IllegalStateException
import java.lang.Integer.parseInt
import javax.inject.Inject

class GetDeckWithCards @Inject constructor(
    private val cardRepo: CardRepository,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {
    operator fun invoke(deckName: String, forceRemoteUpdate: Boolean = false) = flow {
        val deck = cardRepo.getDeck(deckName)
        var uiDeck = deck.toDeckUi()
        val mutex = Mutex()
        val setAsides = ArrayList<CardUi>()

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
                            resource.data ?: emptyList()

                        val setAsideCards = findSetAsides(*cards.toTypedArray())

                        mutex.withLock {
                            setAsides.addAll(setAsideCards)
                        }

                        val slotsMap =
                            deck.slots.associateBy { it.cardOrCode.fetchCode() }.toMutableMap()
                        cards.forEach {
                            slotsMap[it.fetchCode()] =
                                slotsMap[it.fetchCode()]!!.copy(cardOrCode = it)
                        }
                        val uiCards = slotsMap.values.mapNotNull {
                            if (it.isSetAside) {
                                mutex.withLock {
                                    setAsides.add((it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                        format,
                                        it.quantity,
                                        false,
                                    ))
                                }
                                null
                            } else {
                                (it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                    format,
                                    it.quantity,
                                    false,
                                )
                            }
                        }
                        Resource(resource.status, uiCards, resource.isFromDB, resource.message)
                    }

                    val charJob = coroutineScope.async(dispatcher) {
                        val codes = deck.characters.map { slot -> slot.cardOrCode }.toTypedArray()
                        val resource = cardRepo.getCardsByCodes(*codes).last()
                        val cards =
                            resource.data ?: emptyList()

                        val setAsideCards = findSetAsides(*cards.toTypedArray())

                        mutex.withLock {
                            setAsides.addAll(setAsideCards)
                        }

                        val charsMap =
                            deck.characters.associateBy { it.cardOrCode.fetchCode() }.toMutableMap()
                        cards.forEach {
                            charsMap[it.fetchCode()] =
                                charsMap[it.fetchCode()]!!.copy(cardOrCode = it)
                        }
                        val uiCards = charsMap.values.filter { !it.isSetAside }.map {

                                (it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                    format,
                                    it.quantity,
                                    it.isElite,
                                )
                            }

                        val asides = charsMap.values.filter { it.isSetAside }.map {
                            (it.cardOrCode as CardOrCode.HasCard).card.toUi(
                                        format,
                                        it.quantity,
                                        false,
                                    )
                        }


                            mutex.withLock {
                                setAsides.addAll(asides)
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
                            resource.data?.let {
                                setAsides.addAll(findSetAsides(CardOrCode.HasCard(it)))
                            }
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

                            resource.data?.let {
                                val setAsideCard = findSetAsides(CardOrCode.HasCard(it))
                                mutex.withLock { setAsides.addAll(setAsideCard) }
                            }
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
                    var filteredSetAsides = setAsides.filter { setAside -> !(chars.data?.any { it.code == setAside.code } ?: false) }
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

                            uiDeck = uiDeck.copy(chars = chars.data ?: emptyList(), setAsides = filteredSetAsides)
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
                    filteredSetAsides = setAsides.filter { setAside -> !(slots.data?.any { it.code == setAside.code } ?: false) }
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
                            uiDeck = uiDeck.copy(slots = slots.data ?: emptyList(), setAsides = filteredSetAsides)
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
        isElite: Boolean = false,
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
        val balance = format.balance[card.code].asIntPair()
        return if (balance.first != null) {
            balance
        } else {
            card.points
        }

    }

    private suspend inline fun findSetAsides(vararg cards: CardOrCode): List<CardUi> {
        val setAsides = ArrayList<CardUi>()

        cards.forEach { cardOrCode ->
            if (cardOrCode is CardOrCode.HasCard) {
                val text = cardOrCode.card.text ?: ""
                val strings = text.split("<", ">", "[", "]").listIterator()
                while (strings.hasNext()) {
                    val string = strings.next()
                    if (string in CardSetIcon.entries.map { it.code }) {
                        val nextStrings = strings.next().split(")")
                        try {
                            val position = parseInt(nextStrings.first())
                            val resource = cardRepo.getCardBySetAndPosition(string, position).first { it.status != Resource.Status.LOADING }
                            when (resource.status) {
                                Resource.Status.ERROR -> {}
                                else -> if (resource.data != null) setAsides.add(resource.data.toCardUi().copy(quantity = 1))
                            }
                        } catch (e: NumberFormatException) {
                            Log.d("SWD", "Couldn't find an integer for set position")
                        }
                    }

                }
            }
        }
        return setAsides.distinct()
    }
}
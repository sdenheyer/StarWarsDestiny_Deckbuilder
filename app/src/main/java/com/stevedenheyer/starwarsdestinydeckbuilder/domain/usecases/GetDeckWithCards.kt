package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CodeOrCard
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetDeckWithCards @Inject constructor(private val cardRepo: CardRepositoryImpl) {
    operator fun invoke(deckName: String, forceRemoteUpdate: Boolean = false) = flow {
        val deck = cardRepo.getDeck(deckName)

        cardRepo.getCardFormats(forceRemoteUpdate).collect { response ->
            when (response.status) {
                Resource.Status.LOADING -> { emit(UiState.hasData(isLoading = true, errorMessage = null, data = deck.toDeckUi())) }
                Resource.Status.ERROR -> { emit(UiState.hasData(isLoading = false, errorMessage = response.message, data = deck.toDeckUi())) }
                Resource.Status.SUCCESS -> {
                    val format = checkNotNull(response.data?.cardFormats?.find { it.gameTypeName == deck.formatName })
                    val cards = ArrayList<CardUi>()
                    deck.slots.forEach { slot ->
                        val cardResponse = cardRepo.getCardbyCode(slot.cardCode, forceRemoteUpdate).first {it.status != Resource.Status.LOADING }
                            when (cardResponse.status) {
                                Resource.Status.LOADING -> { }
                                Resource.Status.ERROR -> { emit(UiState.hasData(isLoading = false, errorMessage = cardResponse.message, data = deck.toDeckUi())) }
                                Resource.Status.SUCCESS -> {
                                    val card = checkNotNull(cardResponse.data)
                                    var uiCard = card.toCardUi().copy(quantity = slot.quantity)

                                    val reprints = card.reprints.map {
                                        when (it) {
                                            is CodeOrCard.CodeValue -> {
                                                val reprintcard = cardRepo.getCardbyCode(it.value, false).first { it.status == Resource.Status.SUCCESS && it.data != null }  //TODO:  This should be collect - not working as is
                                                CodeOrCard.CardValue(reprintcard.data!!)
                                            }
                                            is CodeOrCard.CardValue -> it
                                        }
                                    }

                                    if (card.setCode in format.includedSets ||
                                        reprints.any { it.value.setCode in format.includedSets })
                                    {
                                        if (card.setCode in format.banned)
                                            uiCard = uiCard.copy(isBanned = true)
                                    } else {
                                        uiCard = uiCard.copy(isBanned = true)
                                    }

                                    val balance = format.balance.get(card.setCode)?.split("/").run {
                                        val first = this?.elementAtOrNull(0)?.toInt()
                                        val second = this?.elementAtOrNull(1)?.toInt()
                                        Pair(first, second)
                                    }
                                    if (balance.first != null) {
                                        uiCard = uiCard.copy(
                                            points = balance,
                                            isRestricted = true
                                        )
                                    }

                                    cards.add(uiCard)
                                }
                            }
                        }
                    val deckUi = deck.toDeckUi().copy(cards = cards)
                    emit(UiState.hasData(isLoading = false, errorMessage = null, data = deckUi))
                }
            }
        }
    }
}
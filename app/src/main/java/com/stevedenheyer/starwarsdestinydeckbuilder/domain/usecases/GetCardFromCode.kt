package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetCardFromCode @Inject constructor(val cardRepo: CardRepositoryImpl) {
    operator fun invoke(
        forceRemoteUpdate: Boolean = false,
        vararg codes: CardOrCode,
    ): Flow<List<CardOrCode>> = flow {
        val cards = ArrayList<CardOrCode>()

        codes.forEach { code ->
            if (code is CardOrCode.hasCard) {
                cards.add(CardOrCode.hasCard(code.card))
            } else {
                val response = cardRepo.getCardbyCode(code.fetchCode(), forceRemoteUpdate)
                    .first { it.status != Resource.Status.LOADING }
                when (response.status) {
                    Resource.Status.LOADING -> {}
                    Resource.Status.ERROR -> {
                        cards.add(CardOrCode.hasCode(code.fetchCode(), msg = response.message))
                    }

                    Resource.Status.SUCCESS -> {
                        val card = checkNotNull(response.data)
                        cards.add(CardOrCode.hasCard(card))
                    }
                }
            }
        }
        emit(cards)
    }
}
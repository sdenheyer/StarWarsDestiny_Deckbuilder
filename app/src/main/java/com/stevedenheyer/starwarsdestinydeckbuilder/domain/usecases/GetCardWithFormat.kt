package com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases

import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.di.IoDispatcher
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asString
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.last
import javax.inject.Inject

class GetCardWithFormat @Inject constructor(private val cardRepo: CardRepositoryImpl,
                                            private val coroutineScope: CoroutineScope,
                                            @IoDispatcher private val dispatcher: CoroutineDispatcher,) {
    operator fun invoke(code: String): Flow<Resource<Card?>> = combineTransform(
        cardRepo.getCardFormats(false),
        cardRepo.getCardByCode(code, false)
    ) { formatsResource, cardResource ->

        if (cardResource.status == Resource.Status.LOADING) {
            emit(cardResource)
        }

        if (formatsResource.status == Resource.Status.SUCCESS && cardResource.status == Resource.Status.SUCCESS && cardResource.data != null) {
            var card = cardResource.data

            val reprintsJob = coroutineScope.async(dispatcher) {
                cardRepo.getCardsByCodes(*card!!.reprints.toTypedArray()).last()
            }

            val parallelDiceJob = coroutineScope.async(dispatcher) {
                cardRepo.getCardsByCodes(*card!!.parallelDiceOf.toTypedArray()).last()
            }

            val reprints = reprintsJob.await().data ?: emptyList()

            val parallelDiceOf = parallelDiceJob.await().data ?: emptyList()

            if (reprints.any { it is CardOrCode.HasCode} || parallelDiceOf.any { it is CardOrCode.HasCode }) {
                emit(Resource.error(msg = formatsResource.message ?: "", data = cardResource.data))
            }

            card = card.copy(reprints = reprints, parallelDiceOf = parallelDiceOf)

            val formatList = ArrayList<Format>()
            val cardFormats = formatsResource.data
            val reprintsSetCodes = card.reprints.mapNotNull {
                when (it) {
                    is CardOrCode.HasCode -> null
                    is CardOrCode.HasCard -> it.card.setCode
                }
            }
            cardFormats?.cardFormats?.forEach {
                var format = Format(it.gameTypeName)
               // Log.d("SWD", "${card.setCode}, ${it}")
                if (card.setCode in it.includedSets ||
                    reprintsSetCodes.any { set ->
                        set in it.includedSets
                    }) {
                    if (card.code in it.banned) {
                        format = format.copy(legality = "banned")
                    }
                    if (card.code in it.restricted || it.restrictedPairs.keys.contains(card.code)) {
                        format = format.copy(legality = "restricted")
                    }
                    format = format.copy(balance = if (it.balance[card.code].isNullOrEmpty()) card.points.asString() else it.balance[card.code])
                } else {
                    format = format.copy(legality = "banned")
                }
                formatList.add(format)
            }
            emit(Resource.success(card.copy(formats = formatList)))
        } else {
            if (formatsResource.status == Resource.Status.ERROR) {
                emit(Resource.error(msg = formatsResource.message ?: "", data = cardResource.data))
            } else {
                if (cardResource.status == Resource.Status.ERROR) {
                    emit(cardResource)
                }
            }
        }
    }
}
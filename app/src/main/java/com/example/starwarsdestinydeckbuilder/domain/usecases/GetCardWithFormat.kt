package com.example.starwarsdestinydeckbuilder.domain.usecases

import android.util.Log
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CodeOrCard
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCardWithFormat @Inject constructor(val cardRepo: CardRepositoryImpl) {
    operator fun invoke(code: String): Flow<Resource<Card?>> = combineTransform(
        cardRepo.getCardFormats(false),
        cardRepo.getCardbyCode(code, false)
    ) { formatsResource, cardResource ->

        if (cardResource.status == Resource.Status.LOADING) {
            emit(cardResource)
        }

        if (formatsResource.status == Resource.Status.SUCCESS && cardResource.status == Resource.Status.SUCCESS && cardResource.data != null) {
            var card = cardResource.data

            val reprints = card.reprints.map {
                when (it) {
                    is CodeOrCard.CodeValue -> {
                        val reprintcard = cardRepo.getCardbyCode(it.value, false).first { it.status == Resource.Status.SUCCESS }
                        CodeOrCard.CardValue(reprintcard.data!!)
                    }
                    is CodeOrCard.CardValue -> it
                }
            }

            val parellelDiceOf = card.parallelDiceOf.map {
                when (it) {
                    is CodeOrCard.CodeValue -> {
                        Log.d("SWD", "Getting parallel: $it")
                        val dicecard = cardRepo.getCardbyCode(it.value, false).first { it.status == Resource.Status.SUCCESS }
                        CodeOrCard.CardValue(dicecard.data!!)
                    }
                    is CodeOrCard.CardValue -> it
                }
            }

            card = card.copy(reprints = reprints, parallelDiceOf = parellelDiceOf)

            val formatList = ArrayList<Format>()
            val cardFormats = formatsResource.data
            val reprintsSetCodes = card?.reprints?.mapNotNull {
                when (it) {
                    is CodeOrCard.CardValue -> it.value.setCode
                    is CodeOrCard.CodeValue -> null
                }
            }
            cardFormats?.cardFormats?.forEach {
                var format = Format(it.gameTypeName)
                Log.d("SWD", "${card.setCode}, ${it}")
                if (card.setCode in it.includedSets ||
                    reprintsSetCodes?.any { code ->
                        code in it.includedSets
                    } ?: false) {
                    if (card.code in it.banned) {
                        format = format.copy(legality = "banned")
                    }
                    if (card.code in it.restricted ||
                        it.restrictedPairs.keys.contains(card.code) == true
                    ) {
                        format = format.copy(legality = "restricted")
                    }
                   // val points:String = if (card.points.isNullOrEmpty()) card.cost.toString() else (card.points ?: "")
                    format = format.copy(balance = if (it.balance[card.code].isNullOrEmpty()) card.points ?: "" else it.balance[card.code])

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
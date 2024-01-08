package com.example.starwarsdestinydeckbuilder.data.local.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.Balance
import com.example.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.example.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatTimeEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.example.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity
import com.example.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.example.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.example.starwarsdestinydeckbuilder.domain.model.CodeOrCard
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class CardCache(
    private val dao: CardsDao
):ICardCache {
    override fun getCardByCode(code: String): Flow<Card?> = dao.getCardByCode(code).map { entity ->
        entity?.toDomain()
    }

    override fun getCardsBySet(code: String): Flow<List<Card>> = dao.getCardsBySet(code).map { it.map { entity -> entity.toDomain() } }

    override fun getCardSets(): Flow<CardSetList> = dao.getCardSets().map {
        val timestamp = dao.getSetTimestamp() ?: CardSetTimeEntity(timestamp = Date().time, expiry = 24 * 60 * 60 * 1000)

        CardSetList(timestamp = timestamp.timestamp, expiry = timestamp.expiry, cardSets = it.map { it.toDomain() })
    }
    override fun getFormats(): Flow<CardFormatList> = dao.getFormats().map {
        list -> CardFormatList(timestamp = Date().time, expiry = 24 * 60 * 60 * 1000, cardFormats = list.map { it.toDomain() })
    }
    override suspend fun storeCards(cards: List<Card>) {
       // Log.d("SWD", "Writing cards: ${cards.size}")
        cards.forEach() { card ->
            val cardEntity = card.toEntity()
         //   Log.d("SWD", "Writing card: ${card.code}, ${card.name}")
            try {
                dao.insertCards(cardEntity)
            } catch(e: SQLiteConstraintException) {
                Log.d("SWD", "Already exists!  ${cardEntity.name}")
                dao.updateCard(cardEntity)
            }
            card.subtypes?.forEach { subtype ->
                dao.insertSubtypes(SubTypeEntity(subtype.code, subtype.name))
                dao.insertCardSubtypesCross(CardSubtypeCrossRef(subTypeCode = subtype.code, code = card.code))
            }
            card.reprints.forEach {
                val cardCode = when (it) {
                    is CodeOrCard.CodeValue -> it.value
                    is CodeOrCard.CardValue -> it.value.code
                }
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertReprints(CardReprintsCrossRef(code = card.code, cardCode = cardCode))
            }
            card.parallelDiceOf.forEach {
                val cardCode = when (it) {
                    is CodeOrCard.CodeValue -> it.value
                    is CodeOrCard.CardValue -> it.value.code
                }
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertParellelDice(CardParellelDiceCrossRef(code = card.code, cardCode = cardCode))
            }
        }
    }

    override suspend fun storeCardSets(sets: CardSetList) {
        sets.cardSets.forEach() {
            val set = it.toEntity()
            try {
                dao.insertCardSets(set)
            } catch(e: SQLiteConstraintException) {
                dao.updateCardSet(set)
            }
        }
        dao.insertSetTimestamp(CardSetTimeEntity(timestamp = sets.timestamp, expiry = sets.expiry))
    }

    override suspend fun storeFormats(formatlist: CardFormatList) {
        formatlist.cardFormats.forEach {
            val format = it.toEntity()
            try {
                dao.insertFormats(format.cardFormat)
            } catch(e: SQLiteConstraintException) {
                dao.updateFormat(format.cardFormat)
            }
            it.includedSets.forEach { set ->
                dao.insertSetCodes(SetCode(set))
                dao.insertIncludedSetsCrossRef(FormatSetCrossref(gameTypeCode = format.cardFormat.gameTypeCode, setCode = set))
            }
            it.balance.forEach { map ->
                dao.insertBalance(Balance(cardCode = map.key, balance = map.value))
                dao.insertBalanceCrossRef(BalanceCardCrossref(gameTypeCode = format.cardFormat.gameTypeCode, cardCode = map.key, balance = map.value))
            }
            it.banned.forEach { code ->
                dao.insertCardCodes(CardCode(code))
                dao.insertBannedCardsCrossRef(FormatBannedCrossref(gameTypeCode = format.cardFormat.gameTypeCode, cardCode = code))
            }
            it.restricted.forEach { code ->
                dao.insertCardCodes(CardCode(code))
                dao.insertRestrictedCardsCrossRef(FormatRestrictedCrossref(gameTypeCode = format.cardFormat.gameTypeCode, cardCode = code))
            }
            it.restrictedPairs.forEach { map ->          //Just saving all the keys - not sure how to handle this exactly
                dao.insertCardCodes(CardCode(map.key))
                dao.insertRestrictedCardsCrossRef(FormatRestrictedCrossref(gameTypeCode = format.cardFormat.gameTypeCode, cardCode = map.key))
            }
        }
        dao.insertFormatTimestamp(FormatTimeEntity(timestamp = formatlist.timestamp, expiry = formatlist.expiry))
    }
}
package com.example.starwarsdestinydeckbuilder.data.local.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.example.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity
import com.example.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardFormat
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CardCache(
    private val dao: CardsDao
):ICardCache {
    override fun getCardByCode(code: String): Flow<Card> = dao.getCardByCode(code).map { entity -> entity.toDomain() }
    override fun getCardsBySet(code: String): Flow<List<Card>> = dao.getCardsBySet(code).map { it.map { entity -> entity.toDomain() } }

    override fun getCardSets(): Flow<List<CardSet>> = dao.getCardSets().map { it.map { it.toDomain() }}
    override fun getFormats(): Flow<List<CardFormat>> = dao.getFormats().map { it.map { it.toDomain() } }
    override suspend fun storeCards(cards: List<Card>) {
        Log.d("SWD", "Writing cards: ${cards.size}")
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
            card.reprints.forEach { cardCode ->
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertReprints(CardReprintsCrossRef(code = card.code, cardCode = cardCode))
            }
            card.parallelDiceOf.forEach { cardCode ->
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertParellelDice(CardParellelDiceCrossRef(code = card.code, cardCode = cardCode))
            }
        }
    }

    override suspend fun storeCardSets(sets: List<CardSet>) {
        sets.forEach() {
            val set = it.toEntity()
            try {
                dao.insertCardSets(set)
            } catch(e: SQLiteConstraintException) {
                dao.updateCardSet(set)
            }
        }
    }

    override suspend fun storeFormats(formats: List<CardFormat>) {
        formats.forEach {
            val format = it.toEntity()
            try {
                dao.insertFormats(format.cardFormat)
            } catch(e: SQLiteConstraintException) {
                dao.updateFormat(format.cardFormat)
            }
        }
    }
}
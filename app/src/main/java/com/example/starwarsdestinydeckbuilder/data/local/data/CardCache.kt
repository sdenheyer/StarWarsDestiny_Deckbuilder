package com.example.starwarsdestinydeckbuilder.data.local.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toEntity
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
    override fun getCardByCode(code: String): Flow<Card> = dao.getCardByCode(code).map { entity -> entity.card.toDomain() }
    override fun getCardsBySet(code: String): Flow<List<Card>> = dao.getCardsBySet(code).map { it.map { entity -> entity.card.toDomain() } }

    override fun getCardSets(): Flow<List<CardSet>> = dao.getCardSets().map { it.map { it.toDomain() }}
    override fun getFormats(): Flow<List<CardFormat>> = dao.getFormats().map { it.map { it.toDomain() } }
    override suspend fun storeCards(cards: List<Card>) {
        Log.d("SWD", "Writing cards: ${cards.size}")
        cards.forEach() {
            val card = it.toEntity()
         //   Log.d("SWD", "Writing card: ${card.code}, ${card.name}")
            try {
                dao.insertCards(card)
            } catch(e: SQLiteConstraintException) {
                Log.d("SWD", "Already exists!  ${card.name}")
                dao.updateCard(card)
            }
            it.subtypes?.forEach {
                dao.insertSubtypes(SubTypeEntity(it.code, it.name))
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
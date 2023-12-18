package com.example.starwarsdestinydeckbuilder.data.local.data

import com.example.starwarsdestinydeckbuilder.data.local.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.local.mappings.toEntity
import com.example.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.sql.SQLClientInfoException

class CardCache(
    private val dao: CardsDao
):ICardCache {
    override fun getCardByCode(code: String): Flow<Card> = dao.getCardByCode(code).map { it.toDomain() }
    override fun getCardsBySet(code: String): Flow<List<Card>> = dao.getCardsBySet(code).map { it.map { it.toDomain() } }

    override suspend fun getCardSets(): Flow<List<CardSet>> = dao.getCardSets().map { it.map { it.toDomain() }}

    override suspend fun storeCards(cards: List<Card>) {
        cards.forEach() {
            val card = it.toEntity()
            try {
                dao.insertCards(card)
            } catch(e: SQLClientInfoException) {
                dao.updateCard(card)
            }
        }
    }

    override suspend fun storeCardSets(sets: List<CardSet>) {
        sets.forEach() {
            val set = it.toEntity()
            try {
                dao.insertCardSets(set)
            } catch(e: SQLClientInfoException) {
                dao.updateCardSet(set)
            }
        }
    }
}
package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.OperatorUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.mappings.toEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.Balance
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardFormatList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.Date

class CardCache(
    private val dao: CardsDao
):ICardCache {
    override fun getCardByCode(code: String): Flow<Card?> = dao.getCardByCode(code).map { entity ->
        entity?.toDomain()
    }

    override fun getCardsBySet(code: String): Flow<List<Card>> =
        dao.getCardsBySet(code).map { it.map { entity -> entity.toDomain() } }

    override fun findCards(query: QueryUi): Flow<List<Card>> =
        dao.findCards(getLocalQueryString(query)).map { it.map { entity -> entity.toDomain() } }

    override fun getCardSets(): Flow<CardSetList> = dao.getCardSets().map {
        val timestamp = dao.getSetTimestamp() ?: CardSetTimeEntity(
            timestamp = Date().time,
            expiry = 24 * 60 * 60 * 1000
        )

        CardSetList(
            timestamp = timestamp.timestamp,
            expiry = timestamp.expiry,
            cardSets = it.map { it.toDomain() })
    }

    override fun getFormats(): Flow<CardFormatList> = dao.getFormats().map { list ->
        CardFormatList(
            timestamp = Date().time,
            expiry = 24 * 60 * 60 * 1000,
            cardFormats = list.map { it.toDomain() })
    }

    override fun getDecks(): Flow<List<Deck>> =
        dao.getDecks().map { it.map { deckEntity -> deckEntity.toDomain() } }

    override suspend fun storeCards(cards: List<Card>) {
        // Log.d("SWD", "Writing cards: ${cards.size}")
        cards.forEach() { card ->
            val cardEntity = card.toEntity()
            //   Log.d("SWD", "Writing card: ${card.code}, ${card.name}")
            try {
                dao.insertCards(cardEntity)
            } catch (e: SQLiteConstraintException) {
                Log.d("SWD", "Already exists!  ${cardEntity.name}")
                dao.updateCard(cardEntity)
            }
            card.subtypes?.forEach { subtype ->
                dao.insertSubtypes(SubTypeEntity(subtype.code, subtype.name))
                dao.insertCardSubtypesCross(
                    CardSubtypeCrossRef(
                        subTypeCode = subtype.code,
                        code = card.code
                    )
                )
            }
            card.reprints.forEach {
                val cardCode = it.fetchCode()
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertReprints(CardReprintsCrossRef(code = card.code, cardCode = cardCode))
            }
            card.parallelDiceOf.forEach {
                val cardCode = it.fetchCode()
                dao.insertCardCodes(CardCode(cardCode))
                dao.insertParellelDice(
                    CardParellelDiceCrossRef(
                        code = card.code,
                        cardCode = cardCode
                    )
                )
            }
        }
    }

    override suspend fun storeCardSets(sets: CardSetList) {
        sets.cardSets.forEach() {
            val set = it.toEntity()
            try {
                dao.insertCardSets(set)
            } catch (e: SQLiteConstraintException) {
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
            } catch (e: SQLiteConstraintException) {
                dao.updateFormat(format.cardFormat)
            }
            it.includedSets.forEach { set ->
                dao.insertSetCodes(SetCode(set))
                dao.insertIncludedSetsCrossRef(
                    FormatSetCrossref(
                        gameTypeCode = format.cardFormat.gameTypeCode,
                        setCode = set
                    )
                )
            }
            it.balance.forEach { map ->
                dao.insertBalance(Balance(cardCode = map.key, balance = map.value))
                dao.insertBalanceCrossRef(
                    BalanceCardCrossref(
                        gameTypeCode = format.cardFormat.gameTypeCode,
                        cardCode = map.key,
                        balance = map.value
                    )
                )
            }
            it.banned.forEach { code ->
                dao.insertCardCodes(CardCode(code))
                dao.insertBannedCardsCrossRef(
                    FormatBannedCrossref(
                        gameTypeCode = format.cardFormat.gameTypeCode,
                        cardCode = code
                    )
                )
            }
            it.restricted.forEach { code ->
                dao.insertCardCodes(CardCode(code))
                dao.insertRestrictedCardsCrossRef(
                    FormatRestrictedCrossref(
                        gameTypeCode = format.cardFormat.gameTypeCode,
                        cardCode = code
                    )
                )
            }
            it.restrictedPairs.forEach { map ->          //Just saving all the keys - not sure how to handle this exactly
                dao.insertCardCodes(CardCode(map.key))
                dao.insertRestrictedCardsCrossRef(
                    FormatRestrictedCrossref(
                        gameTypeCode = format.cardFormat.gameTypeCode,
                        cardCode = map.key
                    )
                )
            }
        }
        dao.insertFormatTimestamp(
            FormatTimeEntity(
                timestamp = formatlist.timestamp,
                expiry = formatlist.expiry
            )
        )
    }

    override suspend fun createDeck(deck: Deck) {
        dao.insertNewDeck(deck.toEntity())
    }

    override suspend fun updateDeck(deck: Deck) {
        dao.updateDeck(deck.toEntity())
    }

    override suspend fun updateDeck(deck: Deck, slot: Slot) {
        if (slot.quantity == 0) {
            Log.d("SWD", "Deleting slot: ${slot.quantity}")
            dao.deleteSlot(slot.toEntity(deck.name))
        } else {
            Log.d("SWD", "Writing new slot: ${slot.quantity}")
            dao.insertSlot(slot.toEntity(deck.name))
        }
        dao.updateDeck(deck.copy(updateDate = Date()).toEntity())
    }

    override suspend fun updateDeck(deck: Deck, char: CharacterCard) {
        if (char.quantity == 0) {
            Log.d("SWD", "Deleting character: ${char.quantity}")
            dao.deleteChar(char.toEntity(deck.name))
        } else {
            Log.d("SWD", "Writing new char: ${char.quantity} ${char.points}")
            dao.insertChar(char.toEntity(deck.name))
        }
        dao.updateDeck(deck.copy(updateDate = Date()).toEntity())
    }

    override suspend fun getDeck(name: String): Deck = dao.getDeck(name).toDomain()

    override fun getOwnedCards(): Flow<List<OwnedCard>> = flow {

        dao.getOwnedCards().collect {
            emit(try {
                it.codes
            } catch (e: NullPointerException) {
                dao.createOwnedCards()
                dao.getOwnedCards().first().codes }.map { it.toDomain() })
        }
    }

    override suspend fun storeOwnedCards(vararg cards: OwnedCard) = dao.insertOwnedCard(*cards.map { it.toEntity() }.toTypedArray())

    private fun getLocalQueryString(query: QueryUi): SupportSQLiteQuery {
        val queryString = StringBuilder("SELECT * FROM cardbaseentity WHERE ")

        query.run {
            if (byCardName.isNotBlank()) {
                queryString.append("name LIKE '%${byCardName}%' AND ")
            }

            if (byColors.size < 4) {
                queryString.append("factionCode = ")
                byColors.forEachIndexed { i, color ->
                    queryString.append("'${color}'")
                    if (i < query.byColors.size - 1)
                        queryString.append(" OR factionCode = ")
                    else
                        queryString.append(" AND ")
                }
            }

            byHealth.run {
                if (number != 0) {
                    queryString.append("health ")
                    when (operator) {
                        OperatorUi.LESS_THAN -> queryString.append("< ")
                        OperatorUi.MORE_THAN -> queryString.append("> ")
                        OperatorUi.EQUALS -> queryString.append("= ")
                    }
                    queryString.append("${number} AND ")
                }
            }

            //TODO: format not included in cardentity - handle in NetworkBoundResource

            byCost.run {
                if (number != 0) {
                    queryString.append("cost ")
                    when (operator) {
                        OperatorUi.LESS_THAN -> queryString.append("< ")
                        OperatorUi.MORE_THAN -> queryString.append("> ")
                        OperatorUi.EQUALS -> queryString.append("= ")
                    }
                    queryString.append("${number} AND ")
                }
            }

            if (bySet.isNotBlank()) {
                queryString.append("setCode = '${bySet}' AND ")
            }

            if (byType.isNotBlank()) {
                queryString.append("typeCode = '${byType}' AND ")
            }

            if (byUnique) {
                queryString.append("isUnique = TRUE AND ")
            }

            if (byCardText.isNotBlank()) {
                queryString.append("text LIKE '%${byCardText}%'")
            }
        }

        if (queryString.endsWith("WHERE ")) {
            return SimpleSQLiteQuery("SELECT * FROM cardbaseentity")   //Corner Case:  Only format selected
        }

        return SimpleSQLiteQuery(queryString.toString().removeSuffix(" AND "))
    }
}
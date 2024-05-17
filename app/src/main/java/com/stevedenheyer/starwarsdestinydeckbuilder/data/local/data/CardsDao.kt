package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.Balance
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardParallelDiceCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CharacterEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CodeQuantity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.OwnedCardsBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.OwnedCardsEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SlotEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardsDao {
    @Insert
    fun insertCards(vararg cards: CardBaseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSubtypes(vararg subType: SubTypeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCardSubtypesCross(vararg subtypeCrossRef: CardSubtypeCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCardCodes(vararg cardCode: CardCode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReprints(vararg reprintsCrossRef: CardReprintsCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertParallelDice(vararg parallelDiceCrossRef: CardParallelDiceCrossRef)

    @Update
    fun updateCard(vararg card: CardBaseEntity)

    @Transaction
    @Query("SELECT * FROM cardbaseentity WHERE setCode = :setCode")
    fun getCardsBySet(setCode: String): Flow<List<CardEntity>>

    @Transaction
    @Query("SELECT * FROM cardbaseentity WHERE code = :code")
    fun getCardByCode(code: String): Flow<CardEntity?>

    @Transaction
    @Query("SELECT * FROM cardbaseentity WHERE code IN (:codes)")
    suspend fun getCardsByCodes(vararg codes: String): List<CardEntity>

    @Transaction
    @RawQuery
    fun findCards(query: SupportSQLiteQuery): Flow<List<CardEntity>>

    @Insert
    fun insertCardSets(vararg sets: CardSetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSetTimestamp(setTimestamp: CardSetTimeEntity)

    @Update
    fun updateCardSet(set: CardSetEntity)

    @Query("SELECT * FROM cardsetentity")
    fun getCardSets(): Flow<List<CardSetEntity>>

    @Query("SELECT * FROM cardsettimeentity")
    suspend fun getSetTimestamp(): CardSetTimeEntity?

    @Insert
    fun insertFormats(vararg formats: FormatBaseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertBalance(vararg balance: Balance)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertSetCodes(vararg setCode: SetCode)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertIncludedSetsCrossRef(vararg formatSetCrossref: FormatSetCrossref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBannedCardsCrossRef(vararg formatBannedCrossref: FormatBannedCrossref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRestrictedCardsCrossRef(vararg formatRestrictedCrossref: FormatRestrictedCrossref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBalanceCrossRef(vararg balanceCardCrossref: BalanceCardCrossref)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormatTimestamp(formatTimestamp: FormatTimeEntity)

    @Update
    fun updateFormat(format: FormatBaseEntity)

    @Query("SELECT * FROM formatbaseentity")
    fun getFormats(): Flow<List<FormatEntity>>

    @Query("SELECT * FROM formattimeentity")
    suspend fun getFormatTimestamp(): FormatTimeEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNewDeck(deck: DeckBaseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChar(charCard: CharacterEntity)

    @Delete
    suspend fun deleteChar(charCard: CharacterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlot(slot: SlotEntity)

    @Delete
    suspend fun deleteSlot(slot: SlotEntity)

    @Update
    suspend fun updateDeck(deck: DeckBaseEntity)

    @Transaction
    @Query("SELECT * FROM deckbaseentity")
    fun getDecks(): Flow<List<DeckEntity>>

    @Transaction
    @Query("SELECT * FROM deckbaseentity WHERE name = :name")
    suspend fun getDeck(name: String): DeckEntity

    @Delete
    fun deleteDeck(deck: DeckBaseEntity)

    @Transaction
    @Query("SELECT * FROM ownedcardsbaseentity WHERE id = 0")
    fun getOwnedCards(): Flow<OwnedCardsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOwnedCard(vararg code: CodeQuantity)

    @Insert
    suspend fun createOwnedCards(ownedCards: OwnedCardsBaseEntity = OwnedCardsBaseEntity())
}
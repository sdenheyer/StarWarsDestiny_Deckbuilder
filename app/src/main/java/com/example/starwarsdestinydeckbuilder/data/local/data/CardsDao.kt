package com.example.starwarsdestinydeckbuilder.data.local.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.starwarsdestinydeckbuilder.data.local.model.Balance
import com.example.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.example.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.example.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity
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
    fun insertParellelDice(vararg parellelDiceCrossRef: CardParellelDiceCrossRef)

    @Update
    fun updateCard(card: CardBaseEntity)

    @Transaction
    @Query("SELECT * FROM cardbaseentity WHERE setCode = :setCode")
    fun getCardsBySet(setCode: String): Flow<List<CardEntity>>

    @Transaction
    @Query("SELECT * FROM cardbaseentity WHERE code = :code")
    fun getCardByCode(code: String): Flow<CardEntity>


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

    @Update
    fun updateFormat(format: FormatBaseEntity)

    @Query("SELECT * FROM formatbaseentity")
    fun getFormats(): Flow<List<FormatEntity>>


}
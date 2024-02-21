package com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.Balance
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CharacterEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.CodeQuantity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.DeckBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.FormatTimeEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.OwnedCardsBaseEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SlotEntity
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity

@Database
    (entities = [CardBaseEntity::class, CardSetEntity::class, CardSubtypeCrossRef::class, CardReprintsCrossRef::class, CardParellelDiceCrossRef::class,
                SubTypeEntity::class, CardCode::class, FormatBaseEntity::class, SetCode::class, Balance::class, FormatSetCrossref::class,
                FormatBannedCrossref::class, FormatRestrictedCrossref::class, BalanceCardCrossref::class, CardSetTimeEntity::class, FormatTimeEntity::class,
                DeckBaseEntity::class, CharacterEntity::class, SlotEntity::class, OwnedCardsBaseEntity::class, CodeQuantity::class,],
            version = 1,
            exportSchema = false
            )

abstract class AppDatabase : RoomDatabase() {
    class Builder(private val application: Application) {
        private val builder: RoomDatabase.Builder<AppDatabase>
            get() = Room.databaseBuilder(application, AppDatabase::class.java, "SWD_DB")
                .fallbackToDestructiveMigration()

        fun build(): AppDatabase = builder.build()
    }

    abstract fun cardsDao(): CardsDao
}

package com.example.starwarsdestinydeckbuilder.data.local.data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.starwarsdestinydeckbuilder.data.local.model.Balance
import com.example.starwarsdestinydeckbuilder.data.local.model.CardBaseEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardCode
import com.example.starwarsdestinydeckbuilder.data.local.model.CardParellelDiceCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardReprintsCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSubtypeCrossRef
import com.example.starwarsdestinydeckbuilder.data.local.model.BalanceCardCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetTimeEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBannedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatBaseEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatRestrictedCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatSetCrossref
import com.example.starwarsdestinydeckbuilder.data.local.model.FormatTimeEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.SetCode
import com.example.starwarsdestinydeckbuilder.data.local.model.SubTypeEntity

@Database
    (entities = [CardBaseEntity::class, CardSetEntity::class, CardSubtypeCrossRef::class, CardReprintsCrossRef::class, CardParellelDiceCrossRef::class,
                SubTypeEntity::class, CardCode::class, FormatBaseEntity::class, SetCode::class, Balance::class, FormatSetCrossref::class,
                FormatBannedCrossref::class, FormatRestrictedCrossref::class, BalanceCardCrossref::class, CardSetTimeEntity::class, FormatTimeEntity::class],
            version = 1,
            exportSchema = false
            )
//@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    class Builder(private val application: Application) {
        private val builder: RoomDatabase.Builder<AppDatabase>
            get() = Room.databaseBuilder(application, AppDatabase::class.java, "SWD_DB")
                .fallbackToDestructiveMigration()

        fun build(): AppDatabase = builder.build()
    }

    abstract fun cardsDao(): CardsDao
}

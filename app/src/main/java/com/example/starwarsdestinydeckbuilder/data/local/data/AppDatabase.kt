package com.example.starwarsdestinydeckbuilder.data.local.data

import android.app.Application
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import kotlinx.coroutines.flow.Flow

@Database
    (entities = [CardEntity::class, CardSetEntity::class],
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

@Dao
interface CardsDao {
    @Insert
    fun insertCards(vararg cards: CardEntity)

    @Update
    fun updateCard(card: CardEntity)

    @Insert
    fun insertCardSets(vararg sets: CardSetEntity)

    @Update
    fun updateCardSet(set: CardSetEntity)

    @Query("SELECT * FROM cardentity WHERE setCode = :setCode")
    fun getCardsBySet(setCode: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cardentity WHERE code = :code")
    fun getCardByCode(code: String): Flow<CardEntity>

    @Query("SELECT * FROM cardsetentity")
    fun getCardSets(): Flow<List<CardSetEntity>>
}
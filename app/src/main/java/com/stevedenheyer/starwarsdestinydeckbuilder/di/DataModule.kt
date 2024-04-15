package com.stevedenheyer.starwarsdestinydeckbuilder.di

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStore
import androidx.datastore.dataStoreFile
import com.stevedenheyer.starwarsdestinydeckbuilder.UserSettings
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardsDao
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.AppDatabase
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.data.userprefs.UserPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideAppDatabase(application: Application) = AppDatabase.Builder(application).build()

    @Provides
    @Singleton
    fun provideCardDao(database: AppDatabase) = database.cardsDao()

    @Provides
    @Singleton
    fun provideCardCache(cardsDao: CardsDao) = CardCache(cardsDao)

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context):DataStore<UserSettings> =
        DataStoreFactory.create(
            serializer = UserPreferencesSerializer,
            produceFile = { context.dataStoreFile("user_prefs.proto")}
        )
}

package com.example.starwarsdestinydeckbuilder.di

import android.app.Application
import com.example.starwarsdestinydeckbuilder.data.local.data.AppDatabase
import com.example.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.example.starwarsdestinydeckbuilder.data.local.data.CardsDao
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardApi
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Binds
    abstract fun provide
}
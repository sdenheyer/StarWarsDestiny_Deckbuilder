package com.stevedenheyer.starwarsdestinydeckbuilder.di

import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)

abstract class RepositoryModule {
    @Binds
    abstract fun bindCardRepository(repositoryImpl: CardRepositoryImpl): CardRepository
    @Binds
    abstract fun bindCardCache(cardCache: CardCache):ICardCache

    @Binds
    abstract fun bindCardNetwork(cardNetwork: CardNetwork):ICardNetwork
}
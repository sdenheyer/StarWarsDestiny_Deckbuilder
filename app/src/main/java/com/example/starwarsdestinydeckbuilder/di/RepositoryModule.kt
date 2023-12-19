package com.example.starwarsdestinydeckbuilder.di

import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.data.local.data.CardCache
import com.example.starwarsdestinydeckbuilder.data.remote.data.CardNetwork
import com.example.starwarsdestinydeckbuilder.domain.data.ICardCache
import com.example.starwarsdestinydeckbuilder.domain.data.ICardNetwork
import com.example.starwarsdestinydeckbuilder.domain.repositories.CardRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindCardCache(cardCache: CardCache):ICardCache

    @Binds
    abstract fun bindCardNetwork(cardNetwork: CardNetwork):ICardNetwork

    @Binds
    abstract fun bindCardRepository(cardRepository: CardRepositoryImpl): CardRepository
}
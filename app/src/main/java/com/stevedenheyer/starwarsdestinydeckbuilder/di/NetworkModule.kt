package com.stevedenheyer.starwarsdestinydeckbuilder.di

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data.CardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    fun provideCardService() = CardApi.retrofitService


}
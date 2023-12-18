package com.example.starwarsdestinydeckbuilder.di

import com.example.starwarsdestinydeckbuilder.data.remote.data.CardApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
object NetworkModule {
    @Provides
    fun provideCardService() = CardApi.retrofitService


}
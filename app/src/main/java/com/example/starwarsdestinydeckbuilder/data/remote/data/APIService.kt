package com.example.starwarsdestinydeckbuilder.data.remote.data

import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import kotlinx.coroutines.flow.Flow

private const val BASE_URL = "https://db.swdrenewedhope.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface CardService {
    @GET("api/public/card/{card_code}")
    suspend fun getCardByCode(@Path("card_code") cardCode: String): Response<CardDTO>

    @GET("api/public/cards/{set_code}")
    suspend fun getCardsBySet(@Path("set_code") setCode: String): Response<List<CardDTO>>

    @GET("api/public/sets/")
    suspend fun getCardSets(): Response<List<CardSetDTO>>

    @GET("api/public/formats")
    suspend fun getFormats(): Response<List<FormatDTO>>
}

object CardApi {
    val retrofitService: CardService by lazy { retrofit.create(CardService::class.java) }
}
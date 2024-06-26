package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.data

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.FormatDTO
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://db.swdrenewedhope.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface CardService {
    @GET("api/public/card/{card_code}")
    suspend fun getCardByCode(@Header("If-modified-since") lastModifiedData: String, @Path("card_code") cardCode: String): Response<CardDTO>

    @GET("api/public/cards/{set_code}")
    suspend fun getCardsBySet(@Header("If-modified-since") lastModifiedData: String, @Path("set_code") setCode: String): Response<List<CardDTO>>

    @GET("api/public/sets/")
    suspend fun getCardSets(@Header("If-modified-since") lastModifiedData: String): Response<List<CardSetDTO>>

    @GET("api/public/formats")
    suspend fun getFormats(@Header("If-modified-since") lastModifiedData: String): Response<List<FormatDTO>>

    @GET("api/public/find")
    suspend fun findCards(@Query("q") query: String): Response<List<CardDTO>>
}

object CardApi {
    val retrofitService: CardService by lazy { retrofit.create(CardService::class.java) }
}
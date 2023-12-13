package com.example.starwarsdestinydeckbuilder.data.retrofit

import com.example.starwarsdestinydeckbuilder.data.retrofit.model.CardJS
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://db.swdrenewedhope.com"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

interface CardAPIService {
    @GET("api/public/card/{card_code}")
    suspend fun getCardByCode(@Path("card_code") cardCode: String): Response<CardJS>
}

object CardApi {
    val retrofitService: CardAPIService by lazy { retrofit.create(CardAPIService::class.java) }
}
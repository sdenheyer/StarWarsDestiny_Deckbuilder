package com.example.starwarsdestinydeckbuilder.data

import com.example.starwarsdestinydeckbuilder.data.retrofit.CardAPIService
import com.example.starwarsdestinydeckbuilder.data.retrofit.CardApi
import com.example.starwarsdestinydeckbuilder.data.retrofit.model.CardJS
import com.example.starwarsdestinydeckbuilder.domain.repositories.CardRepository

class CardRepositoryImpl (): CardRepository {
    override suspend fun getCardbyCode(code: String): CardJS {
        val result = CardApi.retrofitService.getCardByCode(code)
        if (result.isSuccessful) {
            return result.body() ?: CardJS("null")
        } else {
            return CardJS("${result.errorBody()}")
        }
    }
}
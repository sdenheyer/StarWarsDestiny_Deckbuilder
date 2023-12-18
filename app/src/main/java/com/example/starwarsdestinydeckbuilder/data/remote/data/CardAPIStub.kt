package com.example.starwarsdestinydeckbuilder.data.remote.data

import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import retrofit2.Response

class CardAPIStub {
    val testCard = CardDTO.testCard
    fun getCardByCode(code: String): Response<CardDTO> {
        return Response.success(testCard)

    }
}
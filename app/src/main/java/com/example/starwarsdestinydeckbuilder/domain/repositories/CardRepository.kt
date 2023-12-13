package com.example.starwarsdestinydeckbuilder.domain.repositories

import com.example.starwarsdestinydeckbuilder.data.retrofit.model.CardJS
import com.example.starwarsdestinydeckbuilder.domain.model.Card

interface CardRepository {

    suspend fun getCardbyCode(code: String):CardJS


}
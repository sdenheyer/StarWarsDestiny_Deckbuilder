package com.example.starwarsdestinydeckbuilder.data.remote.model

data class CardSetDTO(
    val name: String,
    val code: String,
    val position: Int,
    val available: String,
    val known: Int,
    val total: Int,
    val url: String
)

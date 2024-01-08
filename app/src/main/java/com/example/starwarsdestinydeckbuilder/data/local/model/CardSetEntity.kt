package com.example.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardSetEntity(
    val name: String,
    @PrimaryKey val code: String,
    val position: Int,
    val available: Long,  //Date - should be string?
    val known: Int,
    val total: Int,
    val url: String,
)

@Entity
data class CardSetTimeEntity(
    @PrimaryKey val id: Int = 0,   //Yes, this is on purpose - only want one and replace always
    val timestamp: Long,
    val expiry: Long,
    )
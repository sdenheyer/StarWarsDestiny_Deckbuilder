package com.example.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.net.URL
import java.sql.Date
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

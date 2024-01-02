package com.example.starwarsdestinydeckbuilder.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CardCode(
    @PrimaryKey val cardCode: String
)

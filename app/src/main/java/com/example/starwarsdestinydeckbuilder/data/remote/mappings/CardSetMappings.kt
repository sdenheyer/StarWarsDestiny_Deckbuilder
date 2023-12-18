package com.example.starwarsdestinydeckbuilder.data.remote.mappings

import com.example.starwarsdestinydeckbuilder.data.local.model.CardEntity
import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import java.net.URL
import java.sql.Date
import java.text.SimpleDateFormat

fun CardSetDTO.toDomain() = CardSet(
    name = name,
code = code,
position = position,
available = Date(SimpleDateFormat("yyyy-MM-dd").parse(available).time),
known = known,
total = total,
url = URL(url),
    timestamp = 0
)


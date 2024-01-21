package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSet
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
)


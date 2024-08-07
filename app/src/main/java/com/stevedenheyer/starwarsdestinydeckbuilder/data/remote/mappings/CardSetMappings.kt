package com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings

import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardSetDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSet
import java.net.URL
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

fun CardSetDTO.toDomain() = CardSet(
    name = name,
code = code,
position = position,
available = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(available)?.let { Date(it.time) } ?: Date(0L),
known = known,
total = total,
url = URL(url),
)


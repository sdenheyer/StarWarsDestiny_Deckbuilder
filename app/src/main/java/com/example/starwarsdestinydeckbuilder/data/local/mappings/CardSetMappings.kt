package com.example.starwarsdestinydeckbuilder.data.local.mappings

import com.example.starwarsdestinydeckbuilder.data.local.model.CardSetEntity
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import java.net.URL
import java.sql.Date

fun CardSetEntity.toDomain() = CardSet(
name = name,
code = code,
position = position,
available = Date(available),
known = known,
total = total,
url = URL(url),
timestamp = 0
)

fun CardSet.toEntity() = CardSetEntity(
    name = name,
    code = code,
    position = position,
    available = available.time,
    known = known,
    total = total,
    url = url.toExternalForm(),
)
package com.stevedenheyer.starwarsdestinydeckbuilder.utils

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.stevedenheyer.starwarsdestinydeckbuilder.R

const val DEFAULT_EXPIRY: Long = 24 * 60 * 60 * 1000

val formatMap =
    mapOf("STD" to "Standard", "TRI" to "Trilogy", "INF" to "Infinite", "ARHS" to "ARH Standard")

val affiliationMap = mapOf("hero" to "Hero", "villain" to "Villain", "neutral" to "Neutral")

val typeMap = mapOf(
    "battlefield" to "Battlefield",
    "character" to "Character",
    "downgrade" to "Downgrade",
    "event" to "Event",
    "plot" to "Plot",
    "support" to "Support",
    "upgrade" to "Upgrade"
)

val setCodeMap = mapOf(
    "01" to "AW",
    "02" to "SoR",
    "03" to "EaW",
    "04" to "TPG",
    "05" to "TPG",
    "06" to "RIV",
    "07" to "WotF",
    "08" to "AtG",
    "09" to "AtG",
    "10" to "AoN",
    "11" to "SoH",
    "12" to "CM",
    "13" to "TR",
    "14" to "FA",
    "15" to "RM",
    "16" to "HS",
    "17" to "EoD",
    "18" to "UH",
    "19" to "UH",
    "20" to "SA",
    "21" to "DoP",
    "22" to "RES",
    "23" to "EoD1"
)

val arrowInline = mapOf(
    "dropDownArrow" to InlineTextContent(
        placeholder = Placeholder(
            width = 14.sp,
            height = 24.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    )
    {
        Image(
            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer)
        )
    },
    "pullUpArrow" to InlineTextContent(
        placeholder = Placeholder(
            width = 14.sp,
            height = 24.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    )
    {
        Image(
            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier.rotate(180f)
        )
    },
    "collapsedArrow" to InlineTextContent(
        placeholder = Placeholder(
            width = 14.sp,
            height = 24.sp,
            placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
        )
    )
    {
        Image(
            painter = painterResource(id = R.drawable.baseline_arrow_drop_down_24),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimaryContainer),
            modifier = Modifier.rotate(270f)
        )
    },
)

fun getUniqueInline(size: TextUnit, color: Color): Map<String, InlineTextContent> {
    return mapOf("unique" to InlineTextContent(
        Placeholder(
            width = size,
            height = size,
            placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.unique),
            contentDescription = "Unique",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(color)
        )

    }
    )
}

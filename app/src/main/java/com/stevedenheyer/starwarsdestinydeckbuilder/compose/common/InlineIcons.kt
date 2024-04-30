package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import com.stevedenheyer.starwarsdestinydeckbuilder.R

@Composable
fun getInlines(color: Color = MaterialTheme.colorScheme.onSurface) = mapOf(
    "cards" to InlineTextContent(
        Placeholder(
        width = MaterialTheme.typography.titleLarge.fontSize,
        height = MaterialTheme.typography.titleLarge.fontSize,
        placeholderVerticalAlign = PlaceholderVerticalAlign.Center)
    ) {
        Image(painter = painterResource(id = R.drawable.noun_cards_1212866),
            contentDescription = "cards",
            modifier = Modifier.fillMaxSize().scale(1.1f, 1.2f),
            colorFilter = ColorFilter.tint(color)
        )
    },
    "dice" to InlineTextContent(
        Placeholder(
        width = MaterialTheme.typography.titleLarge.fontSize,
        height = MaterialTheme.typography.titleLarge.fontSize,
        placeholderVerticalAlign = PlaceholderVerticalAlign.Center)
    ) {
        Image(painter = painterResource(id = R.drawable.noun_cube_4025),
            contentDescription = "cards",
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(color)
        )
    }
    ,
    "banned" to InlineTextContent(
        Placeholder(
            width = MaterialTheme.typography.titleLarge.fontSize,
            height = MaterialTheme.typography.titleLarge.fontSize,
            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
        )
    )
    {
            Image(painter = painterResource(id = R.drawable.baseline_cancel_24),
                contentDescription = "banned",
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.error)
            )
        }
    )

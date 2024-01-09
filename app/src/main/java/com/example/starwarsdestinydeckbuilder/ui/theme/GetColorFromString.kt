package com.example.starwarsdestinydeckbuilder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color

fun getColorFromString(s: String): Color {
    when (s) {
        "red" -> return Color.Red
        "blue" -> return Color.Blue
        "gray" -> return Color.Gray
        "yellow" -> return Color.Yellow
        else -> return Color.Unspecified
    }
}
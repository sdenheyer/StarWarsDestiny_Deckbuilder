package com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getColorFromString(s: String): Color {
    when (s) {
        "red" -> return LocalFactionColorScheme.current.factionRed
        "blue" -> return LocalFactionColorScheme.current.factionBlue
        "gray" -> return LocalFactionColorScheme.current.factionGrey
        "yellow" -> return LocalFactionColorScheme.current.factionYellow
        else -> return Color.Unspecified
    }
}
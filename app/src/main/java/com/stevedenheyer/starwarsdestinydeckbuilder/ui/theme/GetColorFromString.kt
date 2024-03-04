package com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun getColorFromString(s: String): Color {
    return when (s) {
        "red" -> LocalFactionColorScheme.current.factionRed
        "blue" -> LocalFactionColorScheme.current.factionBlue
        "gray" -> LocalFactionColorScheme.current.factionGrey
        "yellow" -> LocalFactionColorScheme.current.factionYellow
        else -> Color.Unspecified
    }
}
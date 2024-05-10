package com.stevedenheyer.starwarsdestinydeckbuilder.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Pair<Int?, Int?>.asString():String? {
    return if (this.first == null && this.second == null) {
        null
    } else {
        if (this.second == null) {
            this.first.toString()
        } else {
            this.first.toString() + "/" + this.second.toString()
        }
    }
}

fun String?.asIntPair():Pair<Int?, Int?> {
    return try {
        this?.split("/").run {
            val first = this?.elementAtOrNull(0)?.toInt()
            val second = this?.elementAtOrNull(1)?.toInt()
            Pair(first, second)
        }
    } catch (e: NumberFormatException) {
        Pair(null, null)
    }
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

package com.stevedenheyer.starwarsdestinydeckbuilder.utils

fun Pair<Int?, Int?>.asString():String? {
    if (this.first == null && this.second == null) {
        return null
    } else {
        if (this.second == null) {
            return this.first.toString()
        } else {
            return this.first.toString() + "/" + this.second.toString()
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
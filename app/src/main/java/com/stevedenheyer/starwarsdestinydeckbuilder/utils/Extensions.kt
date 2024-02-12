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
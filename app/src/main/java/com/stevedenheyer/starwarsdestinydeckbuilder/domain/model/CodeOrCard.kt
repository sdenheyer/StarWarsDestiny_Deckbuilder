package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

sealed class CodeOrCard<T>(val value: T) {
    class CardValue(value: Card) : CodeOrCard<Card>(value)
    class CodeValue(value: String) : CodeOrCard<String>(value)
}
package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

sealed interface CardOrCode {
    data class HasCode(val code:String, val msg:String? = null):CardOrCode
    data class HasCard(val card:Card):CardOrCode

    fun fetchCode():String = if (this is HasCard) this.card.code else (this as HasCode).code
}
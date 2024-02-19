package com.stevedenheyer.starwarsdestinydeckbuilder.domain.model

sealed interface CardOrCode {
    data class hasCode(val code:String, val msg:String? = null):CardOrCode
    data class hasCard(val card:Card):CardOrCode

    fun fetchCode():String = if (this is hasCard) this.card.code else (this as hasCode).code
}
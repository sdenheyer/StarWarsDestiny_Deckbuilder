package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

data class SortUi(
    val sortState: SortState = SortState.SET,
    val hideHero: Boolean = false,
    val hideVillain: Boolean = false,
    val gameType: String = ""
)

enum class SortState {
    NAME, SET, FACTION, POINTS_COST,
    HIDE_HERO, HIDE_VILLAIN,
    GAME_TYPE
}

package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

data class SortUi(
    val sortState: SortState = SortState.SET,
    val hideHero: Boolean = true,
    val hideVillain: Boolean = true,
)

enum class SortState {
    NAME, SET, FACTION, POINTS_COST,
    HIDE_HERO, SHOW_VILLAIN
}

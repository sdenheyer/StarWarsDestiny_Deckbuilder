package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

data class SortUi(
    val sortState: SortState = SortState.SET,
    val showHero: Boolean = true,
    val showVillain: Boolean = true,
)

enum class SortState {
    NAME, SET, FACTION, POINTS_COST,
    SHOW_HERO, SHOW_VILLAIN
}

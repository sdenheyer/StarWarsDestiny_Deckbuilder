package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

data class QueryUi(
    val byCardName: String,
    val byCardText: String,

    val byColors: List<String>,

    val byCost: NumericQuery,
    val byHealth: NumericQuery,

    val bySet: String,
    val byFormat: String,
    val byType: String,
    val byUnique: Boolean,
    )

enum class OperatorUi {
    MORE_THAN, EQUALS, LESS_THAN
}

data class NumericQuery(
    val operator: OperatorUi,
    val number: Int,
)

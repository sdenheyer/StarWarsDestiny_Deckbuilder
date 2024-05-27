package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

sealed interface UiState<T>  {
    val isLoading: Boolean
    val errorMessage: String?

    data class NoData<T>(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ): UiState<T>

    data class HasData<T> (
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val isFromDB: Boolean = false,
        val data: T
    ): UiState<T>
}
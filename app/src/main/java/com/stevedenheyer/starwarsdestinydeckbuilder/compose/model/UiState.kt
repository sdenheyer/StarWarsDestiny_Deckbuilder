package com.stevedenheyer.starwarsdestinydeckbuilder.compose.model

sealed interface UiState<T>  {
    val isLoading: Boolean
    val errorMessage: String?

    data class noData<T>(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ): UiState<T>

    data class hasData<T> (
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val isFromDB: Boolean = false,
        val data: T
    ): UiState<T>
}
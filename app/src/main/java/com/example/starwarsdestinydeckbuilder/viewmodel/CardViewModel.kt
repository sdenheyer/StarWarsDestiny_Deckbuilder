package com.example.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.data.retrofit.model.CardJS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CardViewModel constructor(private val cardRepo: CardRepositoryImpl):ViewModel() {

    val _cardFlow = MutableStateFlow(CardJS(""))
    val cardFlow = _cardFlow.asStateFlow()

    init {
        viewModelScope.launch {
            _cardFlow.value = cardRepo.getCardbyCode("01001")
        }
    }
}
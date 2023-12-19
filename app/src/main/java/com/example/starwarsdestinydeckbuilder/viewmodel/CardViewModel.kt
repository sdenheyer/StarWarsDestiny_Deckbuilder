package com.example.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.model.CardSet
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepo: CardRepositoryImpl):ViewModel() {

    private val _cardFlow:MutableStateFlow<Card> = MutableStateFlow(CardDTO.testCard.copy(name = "NOTHING").toDomain())
    val cardFlow = _cardFlow.asStateFlow()

  //  private val _cardSetsFlow:MutableStateFlow<Resource<List<CardSet>>?> = MutableStateFlow(null)
    val cardSetsFlow = cardRepo.getCardSets()

    init {
        viewModelScope.launch {
           // _cardFlow.value = cardRepo.getCardbyCode("01001")
          //  _cardSetsFlow.value = cardRepo.getCardSets()
        }
    }
}
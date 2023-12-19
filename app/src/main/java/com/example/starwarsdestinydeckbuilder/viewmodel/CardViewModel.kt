package com.example.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CardSetMenuItem(
    val code: String,
    val name: String,
    val postition: Int,
)

data class CardUi(
    val code: String,
    val name: String,
    val affiliation: String,
    val faction: String,
    val points: String,
    val health: Int?,
    val type: String,
    val rarity: String,
    val die1: String,
    val die2: String,
    val die3: String,
    val die4: String,
    val die5: String,
    val die6: String,
    val set: String,
)

@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepo: CardRepositoryImpl):ViewModel() {

    private val _cardFlow:MutableStateFlow<Card> = MutableStateFlow(CardDTO.testCard.copy(name = "NOTHING").toDomain())
    val cardFlow = _cardFlow.asStateFlow()

    private val _cardsFlow:MutableStateFlow<List<Card>> = MutableStateFlow(emptyList())
    val cardsFlow = _cardsFlow.map { list ->
        Log.d("SWD", "Generating cardUI: $list")
        list.map { CardUi(
            code = it.code,
            name = it.name,
            affiliation = it.affiliationName ?: "",
            faction = it.factionName,
            points = it.points ?: "",
            health = it.health,
            type = it.typeName,
            rarity = it.rarityName,
            die1 = it.sides?.get(0) ?: "-",
            die2 = it.sides?.get(1) ?: "-",
            die3 = it.sides?.get(2) ?: "-",
            die4 = it.sides?.get(3) ?: "-",
            die5 = it.sides?.get(4) ?: "-",
            die6 = it.sides?.get(5) ?: "-",
            set = it.setName
        )
        }
    }

  //  private val _cardSetsFlow:MutableStateFlow<Resource<List<CardSet>>?> = MutableStateFlow(null)
    val cardSetsFlow = cardRepo.getCardSets()

    val cardSetMenuItemsState = cardSetsFlow.map { response ->
        if (response.status == Resource.Status.SUCCESS) {
            response.data?.map { CardSetMenuItem(code = it.code, name = it.name, postition = it.position) }?.sortedBy { it.postition }
        } else {
            emptyList()
        }
    }

    val _cardSetSelection: MutableStateFlow<String?> = MutableStateFlow(null)
    val cardSetSelection = _cardSetSelection.asStateFlow()

    init {
        viewModelScope.launch {
           // _cardFlow.value = cardRepo.getCardbyCode("01001")
          //  _cardSetsFlow.value = cardRepo.getCardSets()
            cardSetSelection.collect {set ->
                Log.d("SWD", "set selection rec'd: $set")
                if (!set.isNullOrEmpty()) {
                    _cardsFlow.update {
                        val resource = cardRepo.getCardsBySet(set).first { it.status == Resource.Status.SUCCESS && it.data != null && it.data.isNotEmpty()}
                   //     Log.d("SWD", "sending data: ${resource.status} ${resource.data?.size}")
                        resource.data ?: emptyList()
                    }
                }
            }
        }
    }

    fun setCardSetSelection(code: String) {
        _cardSetSelection.update { code }
    }
}
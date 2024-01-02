package com.example.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.data.remote.data.ApiSuccessResponse
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest

import javax.inject.Inject

data class CardSetMenuItem(
    val code: String,
    val name: String,
    val postition: Int,
)

data class CardUi(
    val code: String,
    val name: String,
    val subtitle: String,
    val affiliation: String,
    val faction: String,
    val color: String,
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

fun Card.toCardUi() = CardUi(
    code = code,
    name = name,
    subtitle = subtitle ?: "",
    affiliation = affiliationName ?: "",
    faction = factionName,
    color = factionCode,
    points = points ?: "",
    health = health,
    type = typeName,
    rarity = rarityName,
    die1 = sides?.get(0) ?: "-",
    die2 = sides?.get(1) ?: "-",
    die3 = sides?.get(2) ?: "-",
    die4 = sides?.get(3) ?: "-",
    die5 = sides?.get(4) ?: "-",
    die6 = sides?.get(5) ?: "-",
    set = setName
)

@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepo: CardRepositoryImpl) : ViewModel() {

    private val cardSetSelection = MutableStateFlow("")

    val cardsFlow = cardSetSelection.transformLatest { set ->
        if (set.isNotEmpty()) {
            //Log.d("SWD", "Generating cardUI: $set")
            emitAll(cardRepo.getCardsBySet(set).map { resource ->
                if (resource?.status == Resource.Status.SUCCESS && !resource.data.isNullOrEmpty()) {
                    resource.data.map { it.toCardUi() }
                } else {
                    emptyList()
                }
            })
        }
      }

    val cardSetsFlow = cardRepo.getCardSets()

    val cardSetMenuItemsState = cardSetsFlow.map { response ->
        if (response.status == Resource.Status.SUCCESS) {
            response.data?.map {
                CardSetMenuItem(
                    code = it.code,
                    name = it.name,
                    postition = it.position
                )
            }?.sortedBy { it.postition }
        } else {
            emptyList()
        }
    }

    fun setCardSetSelection(code: String) {
        cardSetSelection.update { code }
    }
}
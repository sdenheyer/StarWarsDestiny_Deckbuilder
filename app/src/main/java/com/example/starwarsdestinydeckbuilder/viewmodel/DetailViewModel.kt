package com.example.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.example.starwarsdestinydeckbuilder.domain.data.Resource
import com.example.starwarsdestinydeckbuilder.domain.model.Card
import com.example.starwarsdestinydeckbuilder.domain.repositories.CardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import java.net.URL
import javax.inject.Inject

data class CardDetailUi(
    val name: String,
    val affiliation: String,
    val faction: String,
    val rarity: String,
    val color: String?,
    val subtitle: String?,
    val typeName: String,
    val subtypes: List<String>?,
    val cost: Int?,
    val points: String?,
    val health: Int?,
    val die1: String,
    val die2: String,
    val die3: String,
    val die4: String,
    val die5: String,
    val die6: String,
    val has_errata: Boolean,
    val text: String?,
    val flavor: String?,
    val illustrator: String?,
    val setName: String,
    val position: Int,
    val reprints: List<String>?,
    val parellelDice: List<String>?,
    val imagesrc: URL,
)

fun Card.toDetailUi() = CardDetailUi(
    name = name,
    affiliation = affiliationName ?: "",
    faction = factionName,
    rarity = rarityName,
    color = factionCode,
    subtitle = subtitle,
    typeName = typeName,
    subtypes = subtypes?.map { it.name },
    cost = cost,
    points = points,
    health = health,
    die1 = sides?.get(0) ?: "-",
    die2 = sides?.get(1) ?: "-",
    die3 = sides?.get(2) ?: "-",
    die4 = sides?.get(3) ?: "-",
    die5 = sides?.get(4) ?: "-",
    die6 = sides?.get(5) ?: "-",
    has_errata = hasErrata,
    text = text,
    flavor = flavor,
    illustrator = illustrator,
    setName = setName,
    position = position,
    reprints = reprints,
    parellelDice = parallelDiceOf,
    imagesrc = imageSrc,
)
@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    cardRepo: CardRepositoryImpl,
) : ViewModel() {

    val code: String = checkNotNull(savedStateHandle.get("code"))

    val card = cardRepo.getCardbyCode(code).transform { resource ->
        if (resource?.status == Resource.Status.SUCCESS && resource.data != null) {
            emit(resource.data.toDetailUi())
        }
    }

    init {
        Log.d("SWD", "Detail code: ${code}")
    }
}
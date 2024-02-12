package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetCardWithFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CodeOrCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import java.net.URL
import javax.inject.Inject

data class CardDetailUi(
    val name: String,
    val code: String,
    val affiliation: String,
    val faction: String,
    val rarity: String,
    val color: String,
    val subtitle: String?,
    val typeName: String,
    val subtypes: List<String>?,
    val points: String?,
    val cost: String?,
    val health: Int?,
    val sides: List<String>?,
    val has_errata: Boolean,
    val text: String?,
    val flavor: String?,
    val illustrator: String?,
    val setName: String,
    val position: Int,
    val reprints: List<MiniCard>,
    val parellelDice: List<MiniCard>,
    val imagesrc: URL,
    val formats: List<Format>,
    val isUnique: Boolean,
    val deckLimit: Int,
)

data class MiniCard(
    val name: String,
    val code: String,
    val setName: String,
    val position: Int
)

fun Card.toDetailUi() = CardDetailUi(
    name = name,
    code = code,
    affiliation = affiliationName ?: "",
    faction = factionName,
    rarity = rarityName,
    color = factionCode,
    subtitle = subtitle,
    typeName = typeName,
    subtypes = subtypes?.map { it.name },
    points = points.asString(),
    cost = cost?.toString(),
    health = health,
    sides = sides,
    has_errata = hasErrata,
    text = text,
    flavor = flavor,
    illustrator = illustrator,
    setName = setName,
    position = position,
    reprints = reprints.mapNotNull {
        when (it) {
            is CodeOrCard.CardValue -> it.value.toMiniCard()
            is CodeOrCard.CodeValue -> null
            }
        },
    parellelDice = parallelDiceOf.mapNotNull {
        when (it) {
            is CodeOrCard.CardValue -> it.value.toMiniCard()
            is CodeOrCard.CodeValue -> null
        }
    },
    imagesrc = imageSrc,
    formats = formats ?: emptyList(),
    isUnique = isUnique,
    deckLimit = deckLimit
)

fun Card.toMiniCard() = MiniCard(
    name = name,
    code = code,
    setName = setName,
    position = position
)

sealed interface CardUiState {
    val isLoading: Boolean
    val errorMessage: String?

    data class noData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ):CardUiState

    data class hasData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val data: CardDetailUi
    ):CardUiState
}

data class DeckDetailUi(
    val name: String,
    val formatName: String,
    val affiliationName: String,
    val quantity: Int,
    val maxQuantity: Int,
    val dice: Int,
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCardWithFormat: GetCardWithFormat,
    private val repo: CardRepositoryImpl,
) : ViewModel() {

    val code: String = checkNotNull(savedStateHandle.get("code"))

    val card = getCardWithFormat(code).stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = Resource.loading())

    val uiCard = card.transform { resource ->
        when (resource.status) {
            Resource.Status.SUCCESS -> {
                if (resource.data != null)
                    emit(CardUiState.hasData(isLoading = false, errorMessage = null, data = resource.data.toDetailUi()))
            }
            Resource.Status.LOADING -> {
                if (resource.data != null)
                    emit(CardUiState.hasData(isLoading = true, errorMessage = null, data = resource.data.toDetailUi()))
                else
                    emit(CardUiState.noData(isLoading = true, errorMessage = null))
            }
            Resource.Status.ERROR -> {
                if (resource.data != null)
                    emit(CardUiState.hasData(isLoading = false, errorMessage = resource.message, data = resource.data.toDetailUi()))
                else
                    emit(CardUiState.noData(isLoading = false, errorMessage = resource.message))
            }
    }
    }.stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = CardUiState.noData(isLoading = true, errorMessage = null))

    private val decks = repo.getAllDecks().stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList())

    val uiDecks = combineTransform(decks, uiCard) { decks, uiCard ->
        if (uiCard is CardUiState.hasData) {
            val card = uiCard.data
            val deckList = decks.map { deck ->
                val quantity = deck.slots.find { it.cardCode == card.code }?.quantity ?: 0
                DeckDetailUi(
                    name = deck.name,
                    formatName = deck.formatName,
                    affiliationName = deck.affiliationName,
                    quantity = quantity,
                    maxQuantity = card.deckLimit,
                    dice = quantity
                )
            }
            emit(deckList)
        }
    }

    fun writeDeck(deckName: String, quantity: Int) {
        val limit = card.value.data?.deckLimit ?: 2
        if (quantity <= limit) {
            Log.d("SWD", "Attempting slot write: ${quantity}, ${limit}")
            val deck = decks.value.find { it.name == deckName }
            if (deck != null) {
                Log.d("SWD", "Writing deck: ${deck.name}")
                val slot = Slot(cardCode = code, quantity = quantity, dice = quantity, dices = null)
                viewModelScope.launch { repo.updateDeck(deck, slot) }
            }
        }

    }
    }


package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetCardWithFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CodeOrCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.transform
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
    points = points ?: if (cost != null) cost.toString() else "",
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
    repo: CardRepositoryImpl,
) : ViewModel() {

    val code: String = checkNotNull(savedStateHandle.get("code"))

    val uiCard = getCardWithFormat(code).transform { resource ->
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
    }

    private val decks = repo.getAllDecks()

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
}
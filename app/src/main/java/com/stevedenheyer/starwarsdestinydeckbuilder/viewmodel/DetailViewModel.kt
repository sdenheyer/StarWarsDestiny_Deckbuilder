package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetCardWithFormat
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CharacterCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.OwnedCard
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Slot
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.first
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
    val hasErrata: Boolean,
    val text: String?,
    val flavor: String?,
    val illustrator: String?,
    val setName: String,
    val setCode: String,
    val position: Int,
    val reprints: List<MiniCard>,
    val parallelDice: List<MiniCard>,
    val imageSrc: URL,
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
    hasErrata = hasErrata,
    text = text,
    flavor = flavor,
    illustrator = illustrator,
    setName = setName,
    setCode = setCode,
    position = position,
    reprints = reprints.mapNotNull {
        when (it) {
            is CardOrCode.HasCard -> it.card.toMiniCard()
            is CardOrCode.HasCode -> null
            }
        },
    parallelDice = parallelDiceOf.mapNotNull {
        when (it) {
            is CardOrCode.HasCard -> it.card.toMiniCard()
            is CardOrCode.HasCode -> null
        }
    },
    imageSrc = imageSrc,
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

    data class NoData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ):CardUiState

    data class HasData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val data: CardDetailUi
    ):CardUiState
}

data class CardDetailDeckUi(
    val name: String,
    val formatName: String,
    val affiliationName: String,

    val quantity: Int,
    val isUnique: Boolean,
    val isElite: Boolean,
    val maxQuantity: Int,
    val plot: String?,
    val battlefield: String?,
    val pointsUsed: Int,
    val deckSize: Int,
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getCardWithFormat: GetCardWithFormat,
    private val repo: CardRepository,
) : ViewModel() {

    val code: String = checkNotNull(savedStateHandle["code"])

    private val cardFlow = getCardWithFormat(code).stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = Resource.loading()
    )

    val uiCard = cardFlow.transform { resource ->
        when (resource.status) {
            Resource.Status.SUCCESS -> {
                if (resource.data != null)
                    emit(
                        CardUiState.HasData(
                            isLoading = false,
                            errorMessage = null,
                            data = resource.data.toDetailUi()
                        )
                    )
            }

            Resource.Status.LOADING -> {
                if (resource.data != null)
                    emit(
                        CardUiState.HasData(
                            isLoading = true,
                            errorMessage = null,
                            data = resource.data.toDetailUi()
                        )
                    )
                else
                    emit(CardUiState.NoData(isLoading = true, errorMessage = null))
            }

            Resource.Status.ERROR -> {
                if (resource.data != null)
                    emit(
                        CardUiState.HasData(
                            isLoading = false,
                            errorMessage = resource.message,
                            data = resource.data.toDetailUi()
                        )
                    )
                else
                    emit(CardUiState.NoData(isLoading = false, errorMessage = resource.message))
            }
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CardUiState.NoData(isLoading = true, errorMessage = null)
    )

    private val decks = repo.getAllDecks()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList())

    val uiDecks = combineTransform(decks, uiCard) { decks, uiCard ->
        if (uiCard is CardUiState.HasData) {
            val card = uiCard.data
            val deckList = decks.map { deck ->

                val quantity = when (card.typeName) {
                    "Character" -> deck.characters.find { it.cardOrCode.fetchCode() == card.code }?.quantity ?: 0
                    "Battlefield" -> if (deck.battlefieldCardCode?.fetchCode() == card.code) 1 else 0
                    "Plot" -> if (deck.plotCardCode?.fetchCode() == card.code) 1 else 0
                    else -> deck.slots.find { it.cardOrCode.fetchCode() == card.code }?.quantity ?: 0
                }

                val isElite = when (card.typeName) {
                    "Character" -> card.isUnique && deck.characters.find { it.cardOrCode.fetchCode() == card.code }?.isElite ?: false
                    "Plot" -> deck.isPlotElite
                    else -> false
                }

                CardDetailDeckUi(
                    name = deck.name,
                    formatName = deck.formatName,
                    affiliationName = deck.affiliationName,

                    quantity = quantity,
                    isUnique = card.isUnique,
                    isElite = isElite,
                    maxQuantity = card.deckLimit,
                    plot = deck.plotCardCode?.fetchCode(),
                    battlefield = deck.battlefieldCardCode?.fetchCode(),
                    pointsUsed = deck.plotPoints + (deck.characters.map {
                        if (it.isElite) it.points
                        else
                            it.points * it.quantity
                    }.reduceOrNull { acc, i -> acc + i } ?: 0),
                    deckSize = deck.slots.map { it.quantity }.reduceOrNull { acc, i -> acc + i }
                        ?: 0
                )
            }
            emit(deckList)
        }
    }

    private val ownedCards = repo.getOwnedCards()
        .stateIn(viewModelScope, started = SharingStarted.Eagerly, initialValue = emptyList())

    val ownedCardsUi = combineTransform(ownedCards, uiCard) { owned, state ->
        if (state is CardUiState.HasData) {
            val card = state.data
            val quantity = owned.find { it.card.fetchCode() == card.code }?.quantity ?: 0

            val ownedDetail = CardDetailDeckUi(
                name = "",
                formatName = "",
                affiliationName = "",

                quantity = quantity,
                isUnique = card.isUnique,
                isElite = false,
                maxQuantity = Int.MAX_VALUE,
                plot = null,
                battlefield = null,
                pointsUsed = 0,
                deckSize = owned.size
            )
            emit(ownedDetail)
        }
    }

    fun writeDeck(deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) {
        val card = (uiCard.value as CardUiState.HasData).data
        when (card.typeName) {
            "Battlefield" -> writeDeck(deckName)
            "Plot" -> writeDeck(deckName, isElite)
            "Character" -> {
                writeDeckWithChar(deckName, quantity, isElite, isSetAside)
            }

            else -> writeDeckWithSlot(deckName, quantity, isSetAside)
        }
    }

    private fun writeDeck(deckName: String, isElite: Boolean = false) {
        var deck = decks.value.find { it.name == deckName }
        if (deck != null) {
            val card = cardFlow.value.data
            //Log.d("SWD","Writing deck: ${deck.name} ${card?.typeCode} ${deck.plotCardCode?.fetchCode() ?: ""} ${isElite}")
            when (card?.typeCode) {
                "battlefield" -> deck = if (deck.battlefieldCardCode?.fetchCode() == card.code)
                    deck.copy(battlefieldCardCode = null)
                else
                    deck.copy(battlefieldCardCode = CardOrCode.HasCode(card.code))

                "plot" -> deck =
                    if (deck.plotCardCode?.fetchCode() == card.code && (deck.isPlotElite || card.points.second == null))
                        deck.copy(plotCardCode = null, plotPoints = 0, isPlotElite = false)
                    else
                        deck.copy(
                            plotCardCode = CardOrCode.HasCode(card.code),
                            isPlotElite = isElite,
                            plotPoints = (if (isElite) card.points.second else card.points.first)
                                ?: 0
                        )
            }
           // Log.d("SWD", "Deck: ${deck}")
            viewModelScope.launch { repo.updateDeck(deck) }
        }
    }

    private fun writeDeckWithChar(deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) {
        val deck = decks.value.find { it.name == deckName }
        val card = try {
            checkNotNull(cardFlow.value.data)
        } catch (e: IllegalStateException) {
            return
        }
        if (deck != null) {
        //    Log.d("SWD", "Writing deck: ${deck.name}, ${quantity}")
            val char = CharacterCard(
                cardOrCode = CardOrCode.HasCode(code),
                points = (if (isElite) card.points.second else card.points.first) ?: 0,
                quantity = quantity,
                isElite = isElite,
                dice = quantity,
                dices = null,
                isSetAside = isSetAside,
            )
            viewModelScope.launch { repo.updateDeck(deck, char) }
        }
    }

    private fun writeDeckWithSlot(deckName: String, quantity: Int, isSetAside: Boolean = false) {
        val limit = cardFlow.value.data?.deckLimit ?: 2
        if (quantity <= limit + 1) {
       //     Log.d("SWD", "Attempting slot write: ${quantity}, ${limit}")
            val deck = decks.value.find { it.name == deckName }
            if (deck != null) {
                Log.d("SWD", "Writing deck: ${deck.name}")
                val slot = Slot(
                    cardOrCode = CardOrCode.HasCode(code),
                    quantity = quantity,
                    dice = if (cardFlow.value.data?.hasDie == true) quantity else 0,
                    dices = null,
                    isSetAside = isSetAside,
                )
                viewModelScope.launch { repo.updateDeck(deck, slot) }
            }
        }
    }

    fun writeOwned(quantity: Int) {
        val code = cardFlow.value.data?.code
        if (code != null) {
            val ownedCard = OwnedCard(card = CardOrCode.HasCode(code), quantity = quantity)
            viewModelScope.launch { repo.insertOwnedCards(ownedCard) }
        }
    }

    suspend fun getCardBySetAndPosition(set: String, position: Int):Card {
        return repo.getCardBySetAndPosition(set, position).first { it.data != null }.data!!
    }

}




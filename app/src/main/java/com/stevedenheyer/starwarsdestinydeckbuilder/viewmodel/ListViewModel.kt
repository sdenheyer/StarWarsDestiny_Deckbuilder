package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch

import javax.inject.Inject

data class menuUiState(
    val isLoading: Boolean,
    val errorMessage: String?,
    val data: List<MenuItem>
) {
    sealed interface MenuItem {
        val code: String
        val name: String

        data class deck(
            override val code: String,
            override val name: String,
        ) : MenuItem

        data class card(
            override val code: String,
            override val name: String,
            val postition: Int
        ) : MenuItem
    }
}

sealed interface UiState {
    val isLoading: Boolean
    val errorMessage: String?

    data class noData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
    ):UiState

    data class hasData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
        val data: List<CardUi>
    ):UiState
}
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
    val diceRef: List<String>,
    val set: String,
)
fun Card.toCardUi() = CardUi(
    code = code,
    name = name,
    subtitle = subtitle ?: "",
    affiliation = affiliationName ?: "",
    faction = factionName,
    color = factionCode,
    points = points ?: if (cost != null) cost.toString() else "",
    health = health,
    type = typeName,
    rarity = rarityName,
    diceRef = sides ?: emptyList(),
    set = setName
)


@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepo: CardRepositoryImpl) : ViewModel() {

    private val cardSetSelection = MutableStateFlow("")
    private val cardsBySetFlow = cardSetSelection.transform { code ->
        if (code.isNotEmpty()) {
            emitAll(cardRepo.getCardsBySet(code, false))
        }
    }

    private val query = MutableStateFlow("")
    private val cardsByQuery = query.transform { query ->
        if (query.isNotEmpty()) {
            emitAll(cardRepo.findCards(query))
        }
    }

    private val _cardsFlow: MutableStateFlow<Resource<List<Card>>> = MutableStateFlow(Resource.loading(emptyList()))

    val cardsFlow = _cardsFlow.mapLatest {
      /*  set ->
        if (set.isNotEmpty()) {
            //Log.d("SWD", "Generating cardUI: $set")
            emitAll(cardRepo.getCardsBySet(set, false).map {
                */
                resource ->
                when (resource.status) {
                    Resource.Status.LOADING -> {
                        if (resource.data.isNullOrEmpty()) {
                            UiState.noData(isLoading = true, errorMessage = resource.message)
                        } else {
                            UiState.hasData(isLoading = true, errorMessage = resource.message, data = resource.data.map { it.toCardUi() })
                        }
                    }
                    Resource.Status.ERROR -> {
                        if (resource.data.isNullOrEmpty()) {
                            UiState.noData(isLoading = false, errorMessage = resource.message)
                        } else {
                            UiState.hasData(isLoading = false, errorMessage = resource.message, data = resource.data.map { it.toCardUi() })
                        }
                    }
                    Resource.Status.SUCCESS -> {
                        if (resource.data.isNullOrEmpty()) {
                            UiState.noData(isLoading = false, errorMessage = resource.message)
                        } else {
                            UiState.hasData(isLoading = false, errorMessage = resource.message, data = resource.data.map { it.toCardUi() })
                        }
                    }
                }
            }

    private val cardSetsFlow = cardRepo.getCardSets(false)

    private val decksFlow = cardRepo.getAllDecks()

    val menuItemsState = combine(cardSetsFlow, decksFlow) { response, decks ->
        val itemsList:MutableList<menuUiState.MenuItem> = decks.map { menuUiState.MenuItem.deck(code = it.name, name = it.name) }.toMutableList()

        if (response.data != null) {
            itemsList.addAll(response.data.cardSets.map {
                menuUiState.MenuItem.card(
                    code = it.code,
                    name = it.name,
                    postition = it.position
                )
            }.sortedBy { it.postition })
        }

        when (response.status) {
            Resource.Status.LOADING -> menuUiState(isLoading = true, errorMessage = response.message, data = itemsList)
            Resource.Status.ERROR -> menuUiState(isLoading = false, errorMessage = response.message, data = itemsList)
            Resource.Status.SUCCESS -> menuUiState(isLoading = false, errorMessage = response.message, data = itemsList)
            }
        }

    init {
        viewModelScope.launch {
            cardsBySetFlow.collect {resource ->
                _cardsFlow.update { resource }
            }
        }

        viewModelScope.launch {
            cardsByQuery.collect {resource ->
                _cardsFlow.update { resource }
            }
        }
    }

    fun setCardSetSelection(code: String) {
        cardSetSelection.update { code }
    }

    fun findCard(queryText: String) {
        query.update { queryText }
    }

    suspend fun createDeck(deck: Deck) = cardRepo.createDeck(deck)
}
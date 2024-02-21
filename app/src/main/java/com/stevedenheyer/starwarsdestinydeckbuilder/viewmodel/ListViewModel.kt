package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.job
import kotlinx.coroutines.launch

import javax.inject.Inject

data class UiCardSet(
    val code: String,
    val name: String,
    val postition: Int
)

data class UiDeck(
    val name: String,
)

@HiltViewModel
class CardViewModel @Inject constructor(private val cardRepo: CardRepositoryImpl) : ViewModel() {

    private val cardSetSelection = MutableStateFlow("")

    private val _cardsFlow: MutableStateFlow<Resource<List<Card>>> = MutableStateFlow(Resource.success(null))

    @OptIn(ExperimentalCoroutinesApi::class)
    val cardsFlow = _cardsFlow.mapLatest { resource ->
        if (resource.data.isNullOrEmpty()) {
            val isLoading = (resource.status == Resource.Status.LOADING)
            UiState.noData(isLoading = isLoading, errorMessage = resource.message)
        } else {
            val data = resource.data.map { it.toCardUi() }
            when (resource.status) {
                Resource.Status.LOADING -> UiState.hasData(
                    isLoading = true,
                    errorMessage = resource.message,
                    data = data
                )

                Resource.Status.ERROR -> UiState.hasData(
                    isLoading = false,
                    errorMessage = resource.message,
                    data = data
                )

                Resource.Status.SUCCESS -> UiState.hasData(
                    isLoading = false,
                    errorMessage = resource.message,
                    data = data
                )
            }
        }
    }.combine(cardRepo.getOwnedCards()) { uiState, ownedCards ->
        if (uiState is UiState.noData || ownedCards.isNullOrEmpty()) {
            uiState
        } else {
            //Do Stuff
            uiState
        }
    }

    private val _decksFlow = cardRepo.getAllDecks().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val decksFlow = _decksFlow.map { decks ->
        decks.map { UiDeck(name = it.name) }
    }

    private val _cardSetsFlow:MutableStateFlow<Resource<CardSetList>> = MutableStateFlow(Resource.success(null))

    val cardSetsFlow = _cardSetsFlow.map  { response ->
        val setList = ArrayList<UiCardSet>()

        if (response.data != null) {
            response.data.cardSets.forEach {set ->
                setList.add(UiCardSet(code = set.code, name = set.name, postition = set.position))
            }
        }

        when (response.status) {
            Resource.Status.LOADING -> UiState.hasData(isLoading = true, errorMessage = response.message, data = setList.toList())
            Resource.Status.ERROR -> UiState.hasData(isLoading = false, errorMessage = response.message, data = setList.toList())
            Resource.Status.SUCCESS -> UiState.hasData(isLoading = false, errorMessage = response.message, data = setList.toList())
            }
        }

    private var cardListJob: Job? = null

    init {
        viewModelScope.launch {
            cardSetSelection.collect {
                refreshCardsBySet(false)
            }
        }

        refreshSets(false)
    }

    fun refreshSets(forceRemoteUpdate: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        cardRepo.getCardSets(forceRemoteUpdate).collect { resource ->
            _cardSetsFlow.update { resource }
        }
    }

    fun refreshCardsBySet(forceRemoteUpdate: Boolean) {
        val code = cardSetSelection.value
        if (code.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                cardListJob?.cancelAndJoin()
                cardListJob = this.coroutineContext.job
                cardRepo.getCardsBySet(code, forceRemoteUpdate).collect { resource ->
                    if (resource.status == Resource.Status.SUCCESS && resource.isFromDB && !resource.data.isNullOrEmpty()) {
                        val numberOfCardsInSet = _cardSetsFlow.value.data?.cardSets?.find { it.code == code }?.known ?: 0
                        if (resource.data.size < numberOfCardsInSet) {
                            refreshCardsBySet(true)
                        }
                    }
                    _cardsFlow.update { resource }
                }
            }
        }
        }

    fun setCardSetSelection(code: String) {
        cardSetSelection.update { code }
    }

    fun findCard(queryText: String) {
        if (queryText.isNotBlank()) {
            cardSetSelection.value = ""
            viewModelScope.launch(Dispatchers.IO) {
                cardListJob?.cancelAndJoin()
                cardListJob = this.coroutineContext.job
                cardRepo.findCards(queryText).collect { resource ->
                        _cardsFlow.update { resource }
                    }
                }
            }
        }

    suspend fun createDeck(deck: Deck) = cardRepo.createDeck(deck)

}
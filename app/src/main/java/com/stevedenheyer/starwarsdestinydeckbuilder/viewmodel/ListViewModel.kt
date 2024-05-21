package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.data.CardRepositoryImpl
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.data.Resource
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Card
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardSetList
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Deck
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.CardRepository
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.CardOrCode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
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

sealed class ListType

class ListTypeNone : ListType()

data class ListTypeBySet(
    val setName: String,
) : ListType()

data class ListTypeByQuery(
    val queryTerms: QueryUi,
) : ListType()

class ListTypeCollection() : ListType()

@HiltViewModel
class ListViewModel @Inject constructor(
    private val cardRepo: CardRepository,
   // private val getCardsFromCodes: GetCardsFromCodes
) : ViewModel() {
    val listTypeFlow: MutableStateFlow<ListType> = MutableStateFlow(ListTypeNone())

    val sortStateFlow = cardRepo.sortStateFlow()

    val savedQueries = cardRepo.fetchSavedQueries()

    private val cardSetSelection = mutableStateOf("")

    private val _cardsFlow: MutableStateFlow<Resource<List<Card>>> =
        MutableStateFlow(Resource.success(null))

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
        when (val state = uiState) {
            is UiState.noData -> uiState
            is UiState.hasData -> {
                val cards = state.data.map { card ->
                    val quantity =
                        (ownedCards.find { it.card.fetchCode() == card.code }?.quantity) ?: 0
                    card.copy(quantity = quantity)
                }
                (uiState as UiState.hasData).copy(data = cards)
            }
        }
    }.combine(sortStateFlow) { uiState, sortState ->
        when (val state = uiState) {
            is UiState.noData -> uiState
            is UiState.hasData -> {
                var cards = state.data
                if (sortState.hideHero) {
                    cards = cards.filterNot { it.affiliation == "Hero" }
                }
                if (sortState.hideVillain) {
                    cards = cards.filterNot { it.affiliation == "Villain" }
                }
                when (sortState.sortState) {
                    SortState.SET -> cards = cards.sortedBy { it.position }.sortedBy { it.set }
                    SortState.NAME -> cards = cards.sortedBy { it.name }
                    SortState.FACTION -> cards = cards.sortedBy { it.faction }
                    SortState.POINTS_COST -> cards =
                        cards.sortedBy { it.cost }.sortedBy { it.points.first }

                    else -> {}
                }
                (uiState as UiState.hasData).copy(data = cards)
            }
        }

    }.flowOn(Dispatchers.IO)

    private val _decksFlow =
        cardRepo.getAllDecks().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val decksFlow = _decksFlow.map { decks ->
        decks.map { UiDeck(name = it.name) }
    }

    private val _cardSetsFlow: MutableStateFlow<Resource<CardSetList>> =
        MutableStateFlow(Resource.loading())

    val cardSetsFlow = _cardSetsFlow.map { response ->
        val setList = ArrayList<UiCardSet>()

        if (response.data != null) {
            response.data.cardSets.forEach { set ->
                setList.add(UiCardSet(code = set.code, name = set.name, postition = set.position))
            }
        }

        when (response.status) {

            Resource.Status.LOADING -> {
                if (setList.isNotEmpty()) {
                    UiState.hasData(
                        isLoading = true,
                        errorMessage = response.message,
                        data = setList.toList()
                    )
                } else {
                    UiState.noData(isLoading = true, errorMessage = response.message)
                }
            }

            Resource.Status.ERROR -> {
                if (setList.isEmpty()) {
                    UiState.noData(
                        isLoading = false,
                        errorMessage = response.message,
                    )
                } else {
                    UiState.hasData(
                        isLoading = false,
                        errorMessage = response.message,
                        data = setList.toList()
                    )
                }
            }

            Resource.Status.SUCCESS -> UiState.hasData(
                isLoading = false,
                errorMessage = response.message,
                data = setList.toList()
            )
        }
    }

    private var cardListJob: Job? = null

    init {

        refreshSets(false)

        showCollection()
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
                        val numberOfCardsInSet =
                            _cardSetsFlow.value.data?.cardSets?.find { it.code == code }?.known
                                ?: 0
                        if (resource.data.size < numberOfCardsInSet) {
                            refreshCardsBySet(true)
                        }
                    }
                    _cardsFlow.update { resource }
                }
            }
        }
    }

    fun refreshList() {
        when (val type = listTypeFlow.value) {
            is ListTypeBySet -> refreshCardsBySet(true)
            is ListTypeByQuery -> findCards(type.queryTerms)
            is ListTypeCollection -> {}
            is ListTypeNone -> {}
        }
    }

    fun setCardSetSelection(code: String) {
        cardSetSelection.value = code
        val setName = _cardSetsFlow.value.data?.cardSets?.find { it.code == code }?.name ?: ""
        listTypeFlow.update { ListTypeBySet(setName = setName) }
        refreshCardsBySet(false)
    }

    fun findCards(query: QueryUi) {
        listTypeFlow.update { ListTypeByQuery(query) }
        cardSetSelection.value = ""
        viewModelScope.launch(Dispatchers.IO) {
            cardListJob?.cancelAndJoin()
            cardListJob = this.coroutineContext.job
            cardRepo.updateSavedNameQueries(query.byCardName)
            cardRepo.updateSavedTextQueries(query.byCardText)
            cardRepo.findCards(query).collect { resource ->
                _cardsFlow.update { resource }
            }
        }
    }

    fun showCollection() {
        listTypeFlow.update { ListTypeCollection() }
        viewModelScope.launch(Dispatchers.IO) {
            cardListJob?.cancelAndJoin()
            cardListJob = this.coroutineContext.job
            cardRepo.getOwnedCards().collect { cards ->

                Log.d("SWD", "Codes: ${cards.map {it.card.fetchCode()}}")

                val cardsMap = cards.associateBy {
                    it.card.fetchCode()
                }.toMutableMap()



                cardRepo.getCardsByCodes(*cardsMap.values.map { it.card }.toTypedArray()).collect { resource ->
                    val cardOrCodeList = resource.data ?: emptyList()
                    cardOrCodeList.forEach {
                        cardsMap[it.fetchCode()] = cardsMap[it.fetchCode()]!!.copy(card = it)
                    }
                    val newResource = Resource(status = resource.status,
                        data = cardsMap.filter { it.value.card is CardOrCode.HasCard }
                        .map { (it.value.card as CardOrCode.HasCard).card.copy(quantity = it.value.quantity) }.toList(),
                        isFromDB = resource.isFromDB, message = resource.message)
                    _cardsFlow.update { newResource }
                }


                /*getCardsFromCodes(false, *cards.toTypedArray()).collect { cards ->
                    _cardsFlow.update {
                        Resource(
                            Resource.Status.SUCCESS,
                            cards.map { (it.card as CardOrCode.hasCard).card },
                            true,
                            message = null
                        )
                }*/
               // cardRepo.getCardsByCodes(cards.toTypedArray())
                /*val list = ArrayList<Card>()
                cards.forEach {
                    val card = getCardFromCode(false, it.card).first().first()

                    when (card) {
                        is CardOrCode.hasCode -> {
                            _cardsFlow.update {
                                Resource(
                                    Resource.Status.ERROR,
                                    list,
                                    true,
                                    message = card.msg
                                )
                            }
                            return@forEach
                        }

                        is CardOrCode.hasCard -> list.add(card.card)
                    }
                }*/

                }
            }
        }

    fun setSort(sortState: SortState) {
        viewModelScope.launch { cardRepo.setSortByState(sortState) }
    }

    suspend fun createDeck(deck: Deck) = cardRepo.createDeck(deck)

}
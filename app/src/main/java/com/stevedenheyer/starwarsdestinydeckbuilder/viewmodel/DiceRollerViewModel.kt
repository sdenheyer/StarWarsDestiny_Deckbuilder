package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetDeckWithCards
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class CardDiceUi(
    val code: String,
    val name: String,
    val color: String,
    val isElite: Boolean,
    val diceRef: List<String>,
    val sideShowing: String? = null,
    val isCardSelected: Boolean,
    val isDieSelected: Boolean = true,
)

fun CardUi.toCardDiceUi(selected: Boolean = false) = CardDiceUi(
    code = code,
    name = name,
    color = color,
    isElite = isElite,
    diceRef = diceRef,
    isCardSelected = selected
)

data class LoadingState(
    val isLoading: Boolean = true,
    val errorMsg: String? = null
)

@HiltViewModel
class DiceRollerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeckWithCards: GetDeckWithCards
) : ViewModel() {

    private val deckCode: String = checkNotNull(savedStateHandle.get("name"))

    val loadingState = MutableStateFlow(LoadingState())

    val cards: MutableStateFlow<List<CardDiceUi>> = MutableStateFlow(emptyList())

    val dice: MutableStateFlow<List<List<CardDiceUi>>> = MutableStateFlow(emptyList())

    init {
        viewModelScope.launch {
            getDeckWithCards(deckCode).collect { deckState ->
                loadingState.update {
                    it.copy(
                        isLoading = deckState.isLoading,
                        errorMsg = deckState.errorMessage
                    )
                }
                when (val state = deckState) {
                    is UiState.NoData -> {}

                    is UiState.HasData -> {
                        val deck = state.data

                        val list = ArrayList<CardDiceUi>()

                        deck.chars.filter { it.diceRef.isNotEmpty() }
                            .forEach {
                                if (it.isElite) {
                                    list.add(it.toCardDiceUi(true))
                                } else {
                                    for (i in 1..it.quantity) {
                                        list.add(it.toCardDiceUi(true))
                                    }
                                }
                            }
                        deck.slots.filter { it.diceRef.isNotEmpty() }
                            .forEach {
                                for (i in 1..it.quantity) {
                                    list.add(it.toCardDiceUi())
                                }
                            }

                        cards.update { list }

                    }
                }
            }
        }

       /* viewModelScope.launch {
            cards.collect { cards ->
                val list = cards.filter { it.isCardSelected }.flatMap {
                    if (it.isElite) {
                        listOf(it, it)
                    } else {
                        listOf(it)
                    }
                }.groupBy { it.code }.values.toList()

                dice.update { list }
            }
        }*/
    }

    fun getDeckName() = deckCode

    fun selectCard(index: Int) {
        cards.update {
            val newList = it.toMutableList()
            val newValue = newList.get(index).copy(isCardSelected = !newList.get(index).isCardSelected)
            newList.removeAt(index)
            newList.add(index, newValue)
            newList
        }
    }

    fun rollAllDice() {
        dice.update {
            val list = cards.value.filter { it.isCardSelected }.flatMap {
                if (it.isElite) {
                    listOf(it, it)
                } else {
                    listOf(it)
                }
            }.groupBy { it.code }.values.toList()

            list.map { list ->
                list.map { die ->
                    if (die.isCardSelected) {
                        die.copy(sideShowing = die.diceRef[Random.nextInt(0, 5)])
                    } else {
                        die
                    }
                }
            }
        }
    }

    fun setOrReroll(code: String, index: Int, side: String?) {  //Null means re-roll
        dice.update {
            it.map { list ->
                list.mapIndexed() { i, die ->
                    if (die.code == code && i == index) {
                        if (side == null) {
                            die.copy(sideShowing = die.diceRef[Random.nextInt(0, 5)])
                        } else {
                            die.copy(sideShowing = side)
                        }
                    } else {
                        die
                    }
                }
            }
        }
    }
}
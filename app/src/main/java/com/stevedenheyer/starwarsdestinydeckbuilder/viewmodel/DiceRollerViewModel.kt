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
    val isDieSelected:Boolean = true,
)

fun CardUi.toCardDiceUi(selected: Boolean = false) = CardDiceUi(
    code = code,
    name = name,
    color = color,
    isElite = isElite,
    diceRef = diceRef,
    isCardSelected = selected
)

data class LoadingState (
    val isLoading: Boolean = true,
    val errorMsg: String? = null
)

@HiltViewModel
class DiceRollerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeckWithCards: GetDeckWithCards
) : ViewModel() {

    val deckCode: String = checkNotNull(savedStateHandle.get("name"))

    val loadingState = MutableStateFlow(LoadingState())

    val cards: MutableStateFlow<List<CardDiceUi>> = MutableStateFlow(emptyList())

    val dice: MutableStateFlow<List<CardDiceUi>> = MutableStateFlow(emptyList())

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
                    is UiState.noData -> {}

                    is UiState.hasData -> {
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

        viewModelScope.launch {
            cards.collect {cards ->
                val list = cards.flatMap {
                    if (it.isElite) {
                        listOf(it, it)
                    } else {
                        listOf(it)
                    }
                }

                dice.update { list }
            }
        }
    }

    fun selectCard(value: String) {
        cards.update {
            it.map { die ->
                if (die.code == value) {
                    die.copy(isCardSelected = !die.isCardSelected)
                } else {
                    die
                }
            }
        }
    }

    fun selectDie(value: Int) {
        dice.update {
            it.mapIndexed { index, die ->
                if (index == value) {
                    die.copy(isDieSelected = !die.isDieSelected)
                } else {
                    die
                }
            }
        }
    }

    fun rollAllDice() {
        dice.update {
            it.map { die ->
                if (die.isCardSelected) {
                    die.copy(sideShowing = die.diceRef[Random.nextInt(0, 5)])
                } else {
                    die
                }
            }
        }
    }

    fun rerollSelectedDice() {
        dice.update {
            it.map { die ->
                if (die.isDieSelected) {
                    die.copy(sideShowing = die.diceRef[Random.nextInt(0, 5)])
                } else {
                    die
                }
            }
        }
    }
}
package com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel

import android.util.Log
import androidx.compose.runtime.internal.composableLambdaN
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.UiState
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.usecases.GetDeckWithCards
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class CardInPlayUi(
    val code: String,
    val name: String,
    val color: String,
    val baseQuantity: Int,
    val maxQuantity: Int,
    val quantity: Int,
    val isSelected: Boolean = false,
)

data class DieUi(
    val color: String,
    val diceRef: List<String>,
    val sideShowing: String? = null,
    val isCopyable: Boolean = true,
)

fun CardUi.toCardInPlayUi() = CardInPlayUi(
    code = code,
    name = name,
    color = color,
    baseQuantity = if (isElite) 2 else 1,
    quantity = if (isElite) 2 else 1,
    maxQuantity = quantity
)

fun CardUi.toDieUi(isCopyable: Boolean) = DieUi(
    color = color,
    diceRef = diceRef,
    isCopyable = isCopyable
)

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

enum class DieRequest() {
    REROLL,
    CHANGE,
    RESOLVE,
    COPY
}

@HiltViewModel
class DiceRollerViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getDeckWithCards: GetDeckWithCards
) : ViewModel() {

    private val deckCode: String = checkNotNull(savedStateHandle["name"])

    val loadingState = MutableStateFlow(LoadingState())

    val cardList: MutableStateFlow<List<CardInPlayUi>> = MutableStateFlow(emptyList())

    val diceMap: MutableStateFlow<Map<String, List<DieUi>>> = MutableStateFlow(emptyMap())

    val cards: MutableStateFlow<List<CardDiceUi>> = MutableStateFlow(emptyList())

    val dice: MutableStateFlow<List<List<CardDiceUi>>> = MutableStateFlow(emptyList())

    init {

        viewModelScope.launch {
            cardList.collect { list ->
                diceMap.update {
                    val map = it.toMutableMap()
                    list.forEach { card ->
                        Log.d("SWD", "Updating dice for card ${card.name} to ${card.quantity}")

                        val dice = map[card.code]!!.toMutableList()
                        while (dice.size < card.quantity) {
                            dice.add(dice.first())
                            Log.d("SWD", "Adding dice... ${dice.size}")
                        }
                        map[card.code] = dice
                    }
                    map
                }
            }
        }

        viewModelScope.launch {
            getDeckWithCards(deckCode).collect { deckState ->
                loadingState.update {
                    it.copy(
                        isLoading = deckState.isLoading,
                        errorMsg = deckState.errorMessage
                    )
                }
                when (deckState) {
                    is UiState.NoData -> {}

                    is UiState.HasData -> {
                        if (!deckState.isLoading) {
                            val deck = deckState.data

                            diceMap.update {
                                val map = it.toMutableMap()

                                deck.chars.filter { it.diceRef.isNotEmpty() }
                                    .forEach { card ->
                                        map[card.code] = listOf(card.toDieUi(true))
                                    }

                                deck.slots.filter { it.diceRef.isNotEmpty() }
                                    .forEach { card ->
                                        map[card.code] = listOf(card.toDieUi(true))
                                    }

                                deck.setAsides.filter { it.diceRef.isNotEmpty() }
                                    .forEach { card ->
                                        map[card.code] = listOf(card.toDieUi(true))
                                    }

                                map
                            }

                            cardList.update {
                                val list = it.toMutableList()

                                deck.chars.filter { it.diceRef.isNotEmpty() }
                                    .forEach {
                                        Log.d("SWD", "Adding card: ${it.name} elite: ${it.isElite}")
                                        list.add(it.toCardInPlayUi())
                                    }

                                deck.slots.filter { it.diceRef.isNotEmpty() }
                                    .forEach {
                                        list.add(it.toCardInPlayUi())
                                    }

                                deck.setAsides.filter { it.diceRef.isNotEmpty() }
                                    .forEach {
                                        list.add(it.toCardInPlayUi())
                                    }

                                list
                            }
                        }
                    }
                }
            }
        }


    }

    fun getDeckName() = deckCode

    fun selectCard(index: Int) {
        cardList.update {
            val list = it.toMutableList()
            val newValue = list[index].copy(isSelected = !list[index].isSelected)
            list.removeAt(index)
            list.add(index, newValue)
            list
        }

        diceMap.update {
            val map = it.toMutableMap()
            val code = cardList.value[index].code

            val newValue = map[code]!!.map {
                val sideShowing =
                    if (cardList.value[index].isSelected) it.diceRef[Random.nextInt(0, 5)] else null
                it.copy(sideShowing = sideShowing)
            }
            map[code] = newValue
            map
        }

        /*  cards.update {
              val newList = it.toMutableList()
              val newValue = newList[index].copy(isCardSelected = !newList[index].isCardSelected)
              newList.removeAt(index)
              newList.add(index, newValue)
              newList
          }*/
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

            list.map { cards ->
                cards.map { die ->
                    if (die.isCardSelected) {
                        die.copy(sideShowing = die.diceRef[Random.nextInt(0, 5)])
                    } else {
                        die
                    }
                }
            }
        }
    }

    fun changeDie(code: String, index: Int, req: DieRequest, side: String? = null) {
        val die = diceMap.value[code]!!.get(index)
        when (req) {
            DieRequest.REROLL -> {
                diceMap.update {
                    val map = it.toMutableMap()
                    val list = map[code]!!.toMutableList()
                    list.add(index, list[index].copy(sideShowing = die.diceRef[Random.nextInt(0, 5)]))
                    list.removeAt(index + 1)
                    map[code] = list
                    map
                }
            }
            DieRequest.CHANGE -> {
                diceMap.update {
                    val map = it.toMutableMap()
                    val list = map[code]!!.toMutableList()
                    list.add(index, list[index].copy(sideShowing = side))
                    list.removeAt(index + 1)
                    map[code] = list
                    map
                }
            }
            DieRequest.RESOLVE -> {
                diceMap.update {
                    val map = it.toMutableMap()
                    val list = map[code]!!.toMutableList()
                    list.add(index, list[index].copy(sideShowing = null))
                    list.removeAt(index + 1)
                    map[code] = list
                    map
                }
            }
            DieRequest.COPY -> {
                try {
                    val card = cardList.value.find { it.code == code }!!
                    if (card.quantity < card.maxQuantity)
                        cardList.update {
                            val list = it.toMutableList()
                            list.add(card.copy(quantity = card.quantity + 1))
                            list
                        }
                } catch(e: NullPointerException) {
                    return
                }
            }
        }
    }

    fun setOrReroll(code: String, index: Int, side: String?) {  //Null means re-roll
        dice.update {
            it.map { list ->
                list.mapIndexed { i, die ->
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
package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.Die
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardInPlayUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DiceRollerViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DieRequest
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DieUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceRollerScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    cardDiceVM: DiceRollerViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {

    val loadingState by cardDiceVM.loadingState.collectAsStateWithLifecycle()
    val cards by cardDiceVM.cardList.collectAsStateWithLifecycle(emptyList())
    val dice by cardDiceVM.diceMap.collectAsStateWithLifecycle(emptyMap())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text((cardDiceVM::getDeckName)()) },
                navigationIcon = {
                    IconButton(onClick = { navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
            )
        },

        bottomBar = {
            BottomAppBar(
                actions = {
                    TextButton(
                        onClick = { (cardDiceVM::resolveAll)() },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    ) {
                        Text("Resolve All")
                    }
                },

                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    ) { padding ->


        Column(
            modifier = modifier
                .padding(padding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer),
        ) {

            DiceGrids(cards = cards,
                diceList = dice,
                isCompactScreen = isCompactScreen,
                selectCard = { index -> (cardDiceVM::selectCard)(index) },
                changeDie = { code, index, request, side -> (cardDiceVM::changeDie)(code, index, request, side)})

            if (loadingState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .width(100.dp),
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun DiceGrids(
    modifier: Modifier = Modifier,
    cards: List<CardInPlayUi>,
    diceList: Map<String, List<DieUi>>,
    isCompactScreen: Boolean,
    selectCard: (Int) -> Unit = {},
    changeDie: (String, Int, DieRequest, String?) -> Unit
) {

    LazyColumn(modifier = modifier) {
        itemsIndexed(items = cards) { cardIndex, card ->

            OutlinedCard(
                onClick = { selectCard(cardIndex) },
                colors = CardDefaults.cardColors(
                    containerColor = if (card.isSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Gray,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(2.dp, getColorFromString(s = card.color)),
                modifier = Modifier.padding(horizontal = 2.dp)
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 4.dp, horizontal = 16.dp)
                )
            }

            LazyVerticalGrid(
                modifier = Modifier.heightIn(128.dp, 1024.dp),
                columns = GridCells.FixedSize(128.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                userScrollEnabled = false
            ) {
                itemsIndexed(items = diceList[card.code] ?: emptyList()) { dieIndex, die ->
                    var dropDownExpanded by remember {
                        mutableStateOf(false)
                    }
                        OutlinedCard(
                            onClick = {
                                if (die.sideShowing != null) dropDownExpanded = !dropDownExpanded else if (!card.isSelected) selectCard(cardIndex)
                                      },
                            colors = CardDefaults.cardColors(
                                containerColor = if (card.isSelected && !die.sideShowing.isNullOrBlank()) MaterialTheme.colorScheme.surfaceContainer else Color.Gray,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            border = BorderStroke(2.dp, getColorFromString(s = die.color)),
                            modifier = Modifier.height(128.dp)
                        )
                        {

                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Die(
                                    modifier = Modifier
                                        .height(64.dp),
                                    dieCode = die.sideShowing ?: "",
                                    isCompactScreen = isCompactScreen
                                )
                            }
                            DropdownMenu(expanded = dropDownExpanded, onDismissRequest = { dropDownExpanded = false }) {
                                DropdownMenuItem(text = { Text("Reroll",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally)) },
                                    onClick = { changeDie(card.code, dieIndex, DieRequest.REROLL, null)
                                                dropDownExpanded = false },
                                )
                                DropdownMenuItem(text = { Text("Resolve",
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally)) },
                                    onClick = { changeDie(card.code, dieIndex, DieRequest.RESOLVE, null)
                                                dropDownExpanded = false },
                                )
                                die.diceRef.forEach {
                                    DropdownMenuItem(
                                        text = { Die(isCompactScreen = isCompactScreen, dieCode = it, modifier = Modifier
                                            .fillMaxWidth()
                                            .wrapContentWidth(align = Alignment.CenterHorizontally)
                                            .height(32.dp)
                                            .width(48.dp)

                                        ) },
                                        onClick = { changeDie(card.code, dieIndex, DieRequest.CHANGE, it)
                                                    dropDownExpanded = false },
                                    )
                                }
                                if (card.quantity < card.maxQuantity)
                                    DropdownMenuItem(text = { Text("Copy",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier.fillMaxWidth().wrapContentWidth(align = Alignment.CenterHorizontally)) },
                                        onClick = { changeDie(card.code, dieIndex, DieRequest.COPY, null)
                                                    dropDownExpanded = false },
                                    )
                            }
                        }
                }
            }
        }
    }
}

@Preview
@Composable
fun DicePreview() {
    DiceGrids(
        isCompactScreen = true,
        diceList = mapOf(
            "" to
            listOf(
                DieUi(
                    color = "Red",
                    diceRef = listOf("+1MD"),
                    sideShowing = null,
                )
            )
        ),
        cards = listOf(CardInPlayUi(
            code = "",
            name = "Darth",
            color = "Red",
            maxQuantity = 4,
            quantity = 2,
            baseQuantity = 1,
            isSelected = true,
        )),
        selectCard = {},
        changeDie = { _, _, _, _ ->  },

    )
}


package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.Die
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.dropDownInline
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.formatMap
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardDiceUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DiceRollerViewModel

@Composable
fun DiceRollerScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    cardDiceVM: DiceRollerViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {

    val loadingState by cardDiceVM.loadingState.collectAsStateWithLifecycle()
    val cardsWithDice by cardDiceVM.dice.collectAsStateWithLifecycle(emptyList())

    DiceRoller(isCompactScreen = isCompactScreen,
        cardsWithDice = cardsWithDice,
        selectCard = { index -> (cardDiceVM::selectCard)(index) },
        selectDie = { index -> (cardDiceVM::selectDie)(index) },
        rollAllDice = { (cardDiceVM::rollAllDice)() },
        rerollSelectedDice = { (cardDiceVM::rerollSelectedDice)() }
    )

}

@Composable
fun DiceRoller(modifier: Modifier = Modifier,
               isCompactScreen: Boolean,
               cardsWithDice: List<CardDiceUi>,
               selectCard: (String) -> Unit = {},
               selectDie: (Int) -> Unit = {},
               rollAllDice: () -> Unit = {},
               rerollSelectedDice: () -> Unit = {}) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(count = if (isCompactScreen) 2 else 4),
            userScrollEnabled = false,
        ) {
            items(items = cardsWithDice.distinctBy { it.code }) { card ->
                OutlinedCard(
                    onClick = { selectCard(card.code) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (card.isCardSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Gray,
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
                            .padding(vertical = 4.dp)
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.FixedSize(128.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            itemsIndexed(items = cardsWithDice) { index, card ->
                if (card.sideShowing != null)
                    OutlinedCard(
                        onClick = { selectDie(index) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (card.isDieSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Gray,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(2.dp, getColorFromString(s = card.color)),
                        modifier = Modifier.height(128.dp)
                    )
                    {
                        Log.d("SWD", "Card: ${card.name} Side: ${card.sideShowing}")

                        Box(modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center) {
                            Die(
                                modifier = Modifier
                                    //   .border(
                                    //       width = 2.dp,
                                    //       color = getColorFromString(card.color))
                                    //  .background(color = if (card.isDieSelected) MaterialTheme.colorScheme.surfaceContainer else Color.Gray)
                                    .height(64.dp)



                                    ,
                                dieCode = card.sideShowing,
                                isCompactScreen = isCompactScreen
                            )
                        }
                    }
            }
        }

        Row(
            modifier.fillMaxSize(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            androidx.compose.material3.Button(
                onClick = { rollAllDice() },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
            ) {
                Text(
                    "Roll All"
                )
            }
            androidx.compose.material3.Button(
                onClick = { rerollSelectedDice() },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
            ) {
                Text(
                    "Re-roll Selected"
                )
            }
        }
    }
}

@Preview
@Composable
fun DiePreview() {
    DiceRoller(isCompactScreen = false, cardsWithDice = listOf(CardDiceUi(
        code = "",
        name = "Darth",
        color = "Red",
        diceRef = listOf("+1MD"),
        sideShowing = "-",
        isCardSelected = true,
        isDieSelected = false,
    )))
}


@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.example.starwarsdestinydeckbuilder.viewmodel.CardUi

@Composable
fun TextList(isCompactScreen: Boolean, cards: List<CardUi>, modifier: Modifier, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items = cards, key = { it.code }) { card ->
            if (!isCompactScreen) {
                CardItem(modifier = Modifier, card = card, onItemClick)
            } else {
                CardItemCompact(modifier = Modifier, card = card, onItemClick)
            }
        }
    }
}


@Composable
fun CardItem(modifier: Modifier, card: CardUi, onItemClick: (String) -> Unit) {
    val borderColor = getColorFromString(card.color)
    OutlinedCard(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, borderColor),
        onClick = { onItemClick(card.code) })
    {
        Row(modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                card.name,
                fontSize = 28.sp,
                color = borderColor,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            if (card.subtitle.isNotBlank()) {
                Text("-", fontSize = 20.sp)
                Text(
                    card.subtitle,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          //  horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Column(modifier = Modifier.weight(2f)) {
                Text("Affiliation", fontSize = 10.sp)
                Text(
                    card.affiliation,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(3f)) {
                Text("Faction", fontSize = 10.sp)
                Text(
                    card.faction,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(2f)) {
                Text("P/C", fontSize = 10.sp)
                Text(
                    card.points,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(1.5f)) {
                Text("Health", fontSize = 10.sp)
                Text(
                    "${card.health ?: "-"}",
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(2.5f)) {
                Text("Type", fontSize = 10.sp)
                Text(
                    card.type,
                    fontSize = 16.sp,
                )
            }

            DieGroup(modifier = Modifier.height(20.dp).weight(9f), dieCodes = card.diceRef, isCompactScreen = false)

            Column(modifier = Modifier.weight(2.5f)) {
                Text("Set", fontSize = 10.sp)
                Text(
                    card.set,
                    fontSize = 16.sp,
                )
            }
        }
      //  Divider()
    }
}

@Composable
fun CardItemCompact(modifier: Modifier, card: CardUi, onItemClick: (String) -> Unit) {
    val cardColor = getColorFromString(card.color)
    OutlinedCard(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, cardColor),
        onClick = { onItemClick(card.code) })
    {
        Row(modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                card.name,
                fontSize = 28.sp,
                color = cardColor,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            if (card.subtitle.isNotBlank()) {
                Text("-", fontSize = 20.sp)
                Text(
                    card.subtitle,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                )
            }
        }
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            //  horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(2f)) {
                Text("Affiliation", fontSize = 10.sp)
                Text(
                    card.affiliation,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(3f)) {
                Text("Faction", fontSize = 10.sp)
                Text(
                    card.faction,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(2f)) {
                Text("P/C", fontSize = 10.sp)
                Text(
                    card.points,
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(1.5f)) {
                Text("Health", fontSize = 10.sp)
                Text(
                    "${card.health ?: "-"}",
                    fontSize = 16.sp,
                )
            }
            Column(modifier = Modifier.weight(2.5f)) {
                Text("Type", fontSize = 10.sp)
                Text(
                    card.type,
                    fontSize = 16.sp,
                )
            }

           /* Column(modifier = Modifier.weight(2.5f)) {
                Text("Set", fontSize = 10.sp)
                Text(
                    card.set,
                    fontSize = 16.sp,
                )
            }*/
        }
        DieGroup(modifier = Modifier.padding(bottom = 10.dp).height(25.dp).fillMaxWidth(), card.diceRef, isCompactScreen = true)
        //  Divider()
    }
}

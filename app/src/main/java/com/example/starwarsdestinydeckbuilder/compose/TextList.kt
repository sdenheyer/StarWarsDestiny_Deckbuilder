package com.example.starwarsdestinydeckbuilder.compose

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val color = when (card.color) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "gray" -> Color.Gray
        "yellow" -> Color.Yellow
        else -> MaterialTheme.colorScheme.background
    }
    Column(modifier = modifier.background(color = color)
        .clickable { onItemClick(card.code) })
    {
        Row(modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                card.name,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.subtitle,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        Row(modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Bottom) {

            Text(
                card.affiliation,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.faction,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.points,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                "${card.health ?: ""}",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.type,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.die1,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die2,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die3,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die4,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die5,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die6,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.set,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Divider()
    }
}

@Composable
fun CardItemCompact(modifier: Modifier, card: CardUi, onItemClick: (String) -> Unit) {
    val color = when (card.color) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "gray" -> Color.Gray
        "yellow" -> Color.Yellow
        else -> MaterialTheme.colorScheme.background
    }
    Column(modifier = modifier.background(color = color)
        .clickable { onItemClick(card.code) })
    {
        Row(modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Bottom) {
            Text(
                card.name,
                fontSize = 28.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.subtitle,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        Row(modifier.padding(vertical = 6.dp), verticalAlignment = Alignment.Bottom) {

            Text(
                card.affiliation,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.faction,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.points,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                "${card.health ?: ""}",
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.type,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
        Row {
            Text(
                card.die1,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die2,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die3,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die4,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die5,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                card.die6,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Text(
                card.set,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
        Divider()
    }
}
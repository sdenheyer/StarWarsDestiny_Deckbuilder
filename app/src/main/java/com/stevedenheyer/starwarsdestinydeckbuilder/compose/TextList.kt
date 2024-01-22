@file:OptIn(ExperimentalMaterial3Api::class)

package com.stevedenheyer.starwarsdestinydeckbuilder.compose

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.toCardUi

@Composable
fun TextList(isCompactScreen: Boolean, cards: List<CardUi>, modifier: Modifier, onItemClick: (String) -> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
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
    val factionColor = getColorFromString(card.color)
    OutlinedCard(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, factionColor),
        onClick = { onItemClick(card.code) })
    {
            Text(buildAnnotatedString {
                withStyle(style = SpanStyle(color = factionColor)) {
                    append(card.name)
                }
                if (card.subtitle.isNotBlank()) {
                    withStyle(style = SpanStyle(
                        fontSize = MaterialTheme.typography.titleMedium.fontSize)) {
                        append("  -  ${card.subtitle}")
                    }
                }

            },
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp))
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          //  horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Column(modifier = Modifier.weight(2f)) {
                Text("Affiliation", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.affiliation,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(3f)) {
                Text("Faction", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.faction,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(2f)) {
                Text("P/C", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.points,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(1.5f)) {
                Text("Health", style = MaterialTheme.typography.labelSmall)
                Text(
                    "${card.health ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(2.5f)) {
                Text("Type", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.type,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            DieGroup(modifier = Modifier
                .height(20.dp)
                .weight(9f), dieCodes = card.diceRef, isCompactScreen = false)

            Column(modifier = Modifier.weight(2.5f)) {
                Text("Set", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.set,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
      //  Divider()
    }
}

@Composable
fun CardItemCompact(modifier: Modifier, card: CardUi, onItemClick: (String) -> Unit) {
    val factionColor = getColorFromString(card.color)
    OutlinedCard(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, factionColor),
        onClick = { onItemClick(card.code) })
    {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(color = factionColor)) {
                append(card.name)
            }
            if (card.subtitle.isNotBlank()) {
                withStyle(style = SpanStyle(
                    fontSize = MaterialTheme.typography.titleMedium.fontSize)) {
                    append("  -  ${card.subtitle}")
                }
            }

        },
            style = MaterialTheme.typography.headlineMedium,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp))
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            //  horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(2f)) {
                Text("Affiliation", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.affiliation,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(3f)) {
                Text("Faction", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.faction,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(2f)) {
                Text("P/C", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.points,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(1.5f)) {
                Text("Health", style = MaterialTheme.typography.labelSmall)
                Text(
                    "${card.health ?: "-"}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Column(modifier = Modifier.weight(2.5f)) {
                Text("Type", style = MaterialTheme.typography.labelSmall)
                Text(
                    card.type,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        DieGroup(modifier = Modifier
            .padding(bottom = 10.dp)
            .height(25.dp)
            .fillMaxWidth(), card.diceRef, isCompactScreen = true)
    }
}

@Preview(widthDp = 800)
@Composable
fun Preview() {
    CardItem(card = CardDTO.testCard.toDomain().toCardUi(), modifier = Modifier) {

    }
}

@Preview(widthDp = 500)
@Composable
fun PreviewCompact() {
    CardItemCompact(card = CardDTO.testCard.toDomain().toCardUi(), modifier = Modifier) {}
}
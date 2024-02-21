package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.DieGroup
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.getInlines
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.toCardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asString

@Composable
fun CardItem(modifier: Modifier, isScreenCompact: Boolean, card: CardUi, onItemClick: (String) -> Unit) =
    if (isScreenCompact) CardItemCompact(modifier = modifier, card = card, onItemClick = onItemClick) else
        CardItemLarge(modifier = modifier, card = card, onItemClick = onItemClick)

@Composable
fun CardItemLarge(modifier: Modifier, card: CardUi, onItemClick: (String) -> Unit) {
    val factionColor = getColorFromString(card.color)
    OutlinedCard(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(2.dp, factionColor),
        onClick = { onItemClick(card.code) })
    {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
            .fillMaxWidth()
            .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = factionColor)) {
                        append(card.name)
                    }
                    if (card.subtitle.isNotBlank()) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        ) {
                            append("  -  ${card.subtitle}")
                        }
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
            )
            if (card.quantity > 0)
                Text(buildAnnotatedString {
                                          append(card.quantity.toString())
                    appendInlineContent("cards", "cards")
                    if (card.diceRef.isNotEmpty()) {
                        val diceQuantity = (if (card.isElite) 2 else card.quantity).toString()
                        append(diceQuantity)
                        appendInlineContent("dice", "dice")
                    }

                },  style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                    inlineContent = getInlines())

        }
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                val pointCostLabel = if (card.cost != null) "Cost" else "Points"
                val pointsOrCost = if (card.cost != null) card.cost.toString() else card.points.asString()
                if (pointsOrCost != null) {
                    Text(pointCostLabel, style = MaterialTheme.typography.labelSmall)
                    Text(
                        pointsOrCost,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
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
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = factionColor)) {
                        append(card.name)
                    }
                    if (card.subtitle.isNotBlank()) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = MaterialTheme.typography.titleMedium.fontSize
                            )
                        ) {
                            append("  -  ${card.subtitle}")
                        }
                    }
                },
                style = MaterialTheme.typography.headlineMedium,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 8.dp, start = 8.dp)
            )
            if (card.quantity > 0)
                Text(buildAnnotatedString {
                    append(card.quantity.toString())
                    appendInlineContent("cards", "cards")
                    if (card.diceRef.isNotEmpty()) {
                        val diceQuantity = (if (card.isElite) 2 else card.quantity).toString()
                        append(diceQuantity)
                        appendInlineContent("dice", "dice")
                    }

                },  style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp, end = 4.dp),
                    inlineContent = getInlines())

        }
        Row(
            modifier
                .padding(top = 0.dp, bottom = 8.dp, start = 8.dp, end = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                val pointCostLabel = if (card.cost != null) "Cost" else "Points"
                val pointsOrCost = if (card.cost != null) card.cost.toString() else card.points.asString()
                if (pointsOrCost != null) {
                    Text(pointCostLabel, style = MaterialTheme.typography.labelSmall)
                    Text(
                        pointsOrCost,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
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
    CardItem(card = CardDTO.testCard.toDomain().toCardUi().copy(quantity = 2), isScreenCompact = false, modifier = Modifier) {

    }
}

@Preview(widthDp = 500)
@Composable
fun PreviewCompact() {
    CardItem(card = CardDTO.testCard.toDomain().toCardUi().copy(quantity = 2), isScreenCompact = true, modifier = Modifier) {}
}
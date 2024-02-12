@file:OptIn(ExperimentalGlideComposeApi::class)

package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardDetailUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardUiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DeckDetailUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DetailViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.toDetailUi
import java.net.URL

@Composable
fun DetailsScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = hiltViewModel()
) {

    val cardState by detailViewModel.uiCard.collectAsStateWithLifecycle(
        initialValue = CardUiState.noData(
            isLoading = true,
            errorMessage = null
        )
    )
    val decks by detailViewModel.uiDecks.collectAsStateWithLifecycle(initialValue = emptyList())

    //  Log.d("SWD", "Card State: ${cardState.isLoading}")

    when (val state = cardState) {
        is CardUiState.hasData -> {
            if (isCompactScreen) {
                CompactDetails(
                    card = state.data,
                    decks = decks
                ) { name, quantity -> (detailViewModel::writeDeck)(name, quantity) }
            } else {
                Details(
                    card = state.data,
                    decks = decks
                ) { name, quantity ->
                    (detailViewModel::writeDeck)(name, quantity)
                }
            }
        }

        is CardUiState.noData -> {}
    }
}

@Composable
fun CompactDetails(
    card: CardDetailUi,
    decks: List<DeckDetailUi>,
    modifier: Modifier = Modifier,
    changeCardQuantity: (deckName: String, quantity: Int) -> Unit
) {
    LazyColumn(modifier) {
        item {
            DetailsCard(modifier = Modifier.padding(vertical = 8.dp), card = card)
        }
        items(items = decks, key = { it.name }) { deck ->
            DeckCardCompact(
                modifier = Modifier.padding(vertical = 8.dp),
                deck = deck
            ) { deckName, quantity -> changeCardQuantity(deckName, quantity) }
        }
        item {
            ImageCard(modifier = Modifier, src = card.imagesrc)
        }
    }
}

@Composable
fun Details(
    card: CardDetailUi,
    decks: List<DeckDetailUi>,
    modifier: Modifier = Modifier,
    changeCardQuantity: (deckName: String, quantity: Int) -> Unit
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailsCard(modifier = Modifier.weight(1f), card = card)
                GlideImage(
                    model = card.imagesrc,
                    contentDescription = "Card Picture",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(horizontal = 2.dp)
                )
            }
        }
        items(items = decks, key = { it.name }) { deck ->
            DeckCard(
                modifier = Modifier.fillMaxWidth(),
                deck = deck
            ) { deckName, quantity -> changeCardQuantity(deckName, quantity) }
        }
    }
}

@Composable
fun DetailsCard(modifier: Modifier, card: CardDetailUi) {

    val cardColor = getColorFromString(card.color)
    val textModifer = Modifier
        .padding(start = 8.dp)
        .padding(vertical = 2.dp)

    val dieInlineContent = DieIcon.entries.associate { die ->
        Pair(die.code, InlineTextContent(
            Placeholder(
                width = MaterialTheme.typography.bodyLarge.fontSize,
                height = MaterialTheme.typography.bodyLarge.fontSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
            )
        ) {
            Image(
                painter = painterResource(id = die.resourceId),
                contentDescription = die.inlineTag,
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        })
    }

    val uniqueInline = mapOf("unique" to InlineTextContent(
        Placeholder(width = MaterialTheme.typography.titleLarge.fontSize,
                    height = MaterialTheme.typography.titleLarge.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline)) {
            Image(
                painter = painterResource(id = R.drawable.unique),
                contentDescription = "Unique",
                modifier = Modifier.fillMaxSize(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        }
    )

    OutlinedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(4.dp, color = cardColor)
    ) {

        Text(
            buildAnnotatedString {
                appendInlineContent("unique", "unique")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = 26.sp)) {
                    append(card.name)
                }
                if (!card.subtitle.isNullOrBlank())
                    withStyle(style = SpanStyle(fontSize = MaterialTheme.typography.titleSmall.fontSize)) {
                        append(" - ${card.subtitle}")
                    }
            },
            style = MaterialTheme.typography.titleLarge,
            overflow = TextOverflow.Ellipsis,
            inlineContent = uniqueInline,
            modifier = Modifier
                .background(color = cardColor)
                .padding(start = 12.dp, top = 12.dp, bottom = 8.dp)
                .fillMaxWidth()
        )

        Text(
            "${card.affiliation}. ${card.faction}. ${card.rarity}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer
        )

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(card.typeName)
                    card.subtypes?.forEach {
                        append(" - ${it}")
                    }
                    append(". ")
                }
                //append("Points: ${card.points}.  Health: ${card.health}")
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer
        )
        val pointCostLabel = if (card.cost != null) "Cost" else "Points"
        val pointsOrCost = if (card.cost != null) card.cost else card.points

        Text(
            buildAnnotatedString {
                if (!pointsOrCost.isNullOrBlank()) {
                    append("${pointCostLabel}: ${pointsOrCost}.")
                }
                if (card.health != null) {
                    append("Health: ${card.health}")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer
        )

        if (card.sides != null) {
            DieGroup(
                modifier = textModifer
                    .padding(vertical = 6.dp)
                    .fillMaxWidth()
                    .height(30.dp),
                dieCodes = card.sides
            )
        }

        if (card.has_errata) {
            Text(
                "This card was errata'd",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = textModifer
            )
        }
        Text(
            parseHtml(card.text ?: ""),
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer,
            inlineContent = dieInlineContent
        )
        Text(
            card.flavor ?: "",
            style = MaterialTheme.typography.bodyMedium,
            modifier = textModifer,
            fontStyle = FontStyle.Italic
        )
        if (!card.illustrator.isNullOrEmpty()) {
            Row(modifier = textModifer) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_brush_24),
                    contentDescription = "Paintbrush Icon",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(card.illustrator, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text(
            "${card.setName} #${card.position}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer
        )

        if (card.reprints.isNotEmpty()) {
            Text(
                "Reprinted in:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifer
            )
            card.reprints.forEach {
                Text(
                    "${it.setName} #${it.position}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = textModifer,
                    color = Color.Blue
                )
            }
        }

        if (card.parellelDice.isNotEmpty()) {
            Text(
                "Used as parallel die by:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifer
            )
            card.parellelDice.forEach {
                Text(
                    "${it.setName} #${it.position}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = textModifer
                )
            }
        }

        if (card.formats.isNotEmpty()) {
            Legality(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                factionColor = cardColor,
                formats = card.formats
            )
            if (!card.formats.all { format -> format.balance.isNullOrBlank() }) {
                Balance(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                    factionColor = cardColor,
                    formats = card.formats
                )
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageCard(modifier: Modifier, src: URL) {
    GlideImage(
        model = src,
        contentDescription = "",
        contentScale = ContentScale.FillWidth,
        alignment = Alignment.TopCenter,
        modifier = modifier.padding(horizontal = 6.dp)
    )
}

@Composable
fun Legality(modifier: Modifier, factionColor: Color, formats: List<Format>) {
    val border = BorderStroke(width = Dp.Hairline, color = MaterialTheme.colorScheme.onSurface)
    Log.d("SWD", "Legality: ${formats.size} ${formats.toString()}")
    Column(modifier = modifier) {
        Text(
            "Legality", style = MaterialTheme.typography.titleLarge, modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )

        LazyRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        ) {
            items(items = formats, key = { it.gameType }) { format ->

                val columnModifier = if (format.legality == "banned")
                    Modifier
                        .fillMaxWidth()
                        .border(border)
                else Modifier
                    .fillMaxWidth()
                    .border(border)
                    .background(color = Color.Green)

                Column(
                    modifier = columnModifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(border = border, color = factionColor) {
                        Text(
                            format.gameType,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }
                    if (format.legality == "banned")
                        Image(
                            painter = painterResource(id = R.drawable.baseline_cancel_24),
                            contentDescription = "Banned",
                            colorFilter = ColorFilter.tint(factionColor),
                            modifier = Modifier.padding(vertical = 4.dp),
                        )
                    else
                        Image(
                            painter = painterResource(id = R.drawable.baseline_check_circle_24),
                            contentDescription = "Allowed",
                            colorFilter = ColorFilter.tint(factionColor),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                }
            }
        }
    }
}

@Composable
fun Balance(modifier: Modifier, factionColor: Color, formats: List<Format>) {
    val border = BorderStroke(width = Dp.Hairline, color = MaterialTheme.colorScheme.onSurface)
    Column(modifier = modifier) {
        Text(
            "Balance of the Force", style = MaterialTheme.typography.titleLarge, modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )

        LazyRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        ) {
            items(items = formats, key = { it.gameType }) { format ->

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(border), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(border = border, color = factionColor) {
                        Text(
                            format.gameType,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                        )
                    }
                    val legalityText =
                        if (format.legality == "banned") "--" else format.balance ?: "--"

                    Text(
                        legalityText,
                        style = MaterialTheme.typography.bodyLarge, modifier = Modifier
                            .padding(vertical = 2.dp)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DeckCard(
    modifier: Modifier,
    deck: DeckDetailUi,
    changeQuantity: (deckName: String, quantity: Int) -> Unit
) {
    val textModifer = Modifier
        .padding(vertical = 2.dp, horizontal = 8.dp)
    OutlinedCard(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(4.dp, color = MaterialTheme.colorScheme.onSurface)
    ) {
        Row(modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                deck.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = textModifer.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                deck.formatName,
                style = MaterialTheme.typography.titleMedium,
                modifier = textModifer.weight(0.6f)
            )
            Text(
                deck.affiliationName,
                style = MaterialTheme.typography.titleMedium,
                modifier = textModifer.weight(0.4f)
            )
            TextButton(border = BorderStroke(
                2.dp,
                color = MaterialTheme.colorScheme.primary
            ),
                enabled = (deck.quantity > 0),
                onClick = { changeQuantity(deck.name, deck.quantity - 1) }) {
                Text(
                    "-",
                    Modifier
                        .wrapContentSize(align = Alignment.Center),
                    softWrap = true,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Text(
                deck.quantity.toString(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            TextButton(border = BorderStroke(
                2.dp,
                color = MaterialTheme.colorScheme.primary,
            ),
                enabled = (deck.quantity < deck.maxQuantity),
                onClick = { changeQuantity(deck.name, deck.quantity + 1) }) {
                Text(
                    "+",
                    Modifier
                        .wrapContentSize(align = Alignment.Center),
                    softWrap = true,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun DeckCardCompact(
    modifier: Modifier,
    deck: DeckDetailUi,
    changeQuantity: (deckName: String, quantity: Int) -> Unit
) {
    val textModifer = Modifier
        .padding(vertical = 2.dp, horizontal = 8.dp)
    OutlinedCard(
        modifier = modifier
            .padding(top = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(4.dp, color = MaterialTheme.colorScheme.onSurface)
    ) {
        Row(modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
                Text(
                    deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = textModifer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        deck.formatName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = textModifer.width(120.dp)
                    )
                    Text(
                        deck.affiliationName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = textModifer
                    )
                }
            }
            Row {
                TextButton(border = BorderStroke(
                    2.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                    enabled = (deck.quantity > 0),
                    onClick = { changeQuantity(deck.name, deck.quantity - 1) }) {
                    Text(
                        "-",
                        Modifier
                            .wrapContentSize(align = Alignment.Center),
                        softWrap = true,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Text(
                    deck.quantity.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                TextButton(border = BorderStroke(
                    2.dp,
                    color = MaterialTheme.colorScheme.primary,
                ),
                    enabled = (deck.quantity < deck.maxQuantity),
                    onClick = { changeQuantity(deck.name, deck.quantity + 1) }) {
                    Text(
                        "+",
                        Modifier
                            .wrapContentSize(align = Alignment.Center),
                        softWrap = true,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
    }
}

fun parseHtml(s: String): AnnotatedString {
    val strings = s.split("<", ">", "[", "]").listIterator()
    return buildAnnotatedString {
        while (strings.hasNext()) {
            val string = strings.next()
            when (string) {
                "b" -> withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(strings.next())
                }

                "/b" -> {}

                "i" -> withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(strings.next())
                }

                "/i" -> {}

                "em" -> withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append(strings.next())
                }

                "/em" -> {}

                DieIcon.BLANK.inlineTag -> appendInlineContent(
                    DieIcon.BLANK.code,
                    "[" + DieIcon.BLANK.inlineTag + "]"
                )

                DieIcon.DISCARD.inlineTag -> appendInlineContent(
                    DieIcon.DISCARD.code,
                    "[" + DieIcon.DISCARD.inlineTag + "]"
                )

                DieIcon.DISRUPT.inlineTag -> appendInlineContent(
                    DieIcon.DISRUPT.code,
                    "[" + DieIcon.DISRUPT.inlineTag + "]"
                )

                DieIcon.FOCUS.inlineTag -> appendInlineContent(
                    DieIcon.FOCUS.code,
                    "[" + DieIcon.FOCUS.inlineTag + "]"
                )

                DieIcon.INDIRECT.inlineTag -> appendInlineContent(
                    DieIcon.INDIRECT.code,
                    "[" + DieIcon.INDIRECT.inlineTag + "]"
                )

                DieIcon.MELEE.inlineTag -> appendInlineContent(
                    DieIcon.MELEE.code,
                    "[" + DieIcon.MELEE.inlineTag + "]"
                )

                DieIcon.RANGED.inlineTag -> appendInlineContent(
                    DieIcon.RANGED.code,
                    "[" + DieIcon.RANGED.inlineTag + "]"
                )

                DieIcon.RESOURCE.inlineTag -> appendInlineContent(
                    DieIcon.RESOURCE.code,
                    "[" + DieIcon.RESOURCE.inlineTag + "]"
                )

                DieIcon.SHIELD.inlineTag -> appendInlineContent(
                    DieIcon.SHIELD.code,
                    "[" + DieIcon.SHIELD.inlineTag + "]"
                )

                DieIcon.SPECIAL.inlineTag -> appendInlineContent(
                    DieIcon.SPECIAL.code,
                    "[" + DieIcon.SPECIAL.inlineTag + "]"
                )

                else -> append(string)
            }
        }
    }
}

/*@Preview
@Composable
fun HtmlParser() {
    val t =
        parseHtml("Blue Character only.\n<b>Action</b> - Remove this die to turn a die to a side showing a blank ([blank]).")
    Text(t, inlineContent = CardText.inlineContent)

}*/

@Preview(widthDp = 500)
@Composable
fun FormatPreview() {
    val formats = listOf(
        Format(gameType = "Standard", legality = "banned", balance = "12/16"),
        Format(gameType = "Trilogy", legality = "", balance = "12/16"),
        Format(gameType = "Infinite", legality = "", balance = "12/16"),
        Format(gameType = "ARH Standard", legality = "", balance = "12/16")
    )
    Column {
        Legality(modifier = Modifier, factionColor = Color.Red, formats = formats)
        Balance(modifier = Modifier, formats = formats, factionColor = Color.Red)
    }

}

@Preview(widthDp = 400)
@Composable
fun TextCardPreview() {
    DetailsCard(
        modifier = Modifier
            .background(Color.Black)
            .fillMaxSize(), card = CardDTO.testCard.toDomain().toDetailUi()
    )
}

@Preview(widthDp = 700)
@Composable
fun DecksPreview() {
    val decks = listOf(
        DeckDetailUi(
            name = "test",
            affiliationName = "Hero",
            formatName = "Infinite",
            quantity = 0,
            maxQuantity = 2,
            dice = 0
        ),
        DeckDetailUi(
            name = "Loooooooongname",
            affiliationName = "Villain",
            formatName = "ARH Standard",
            quantity = 0,
            maxQuantity = 2,
            dice = 0
        )
    )
    val deck = DeckDetailUi(
        name = "test",
        affiliationName = "Hero",
        formatName = "Infinite",
        quantity = 0,
        maxQuantity = 2,
        dice = 0
    )
    DeckCard(modifier = Modifier, deck = deck) { name, quan -> }
}

@Preview(widthDp = 500)
@Composable
fun DecksCompactPreview() {
    val decks = listOf(
        DeckDetailUi(
            name = "test",
            affiliationName = "Hero",
            formatName = "Infinite",
            quantity = 0,
            maxQuantity = 2,
            dice = 0
        ),
        DeckDetailUi(
            name = "Loooooooongname",
            affiliationName = "Villain",
            formatName = "ARH Standard",
            quantity = 0,
            maxQuantity = 2,
            dice = 0
        )
    )
    val deck = DeckDetailUi(
        name = "test",
        affiliationName = "Hero",
        formatName = "Infinite",
        quantity = 0,
        maxQuantity = 2,
        dice = 0
    )
    DeckCardCompact(modifier = Modifier, deck = deck) { name, quan -> }
}


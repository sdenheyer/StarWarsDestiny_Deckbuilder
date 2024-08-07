@file:OptIn(ExperimentalGlideComposeApi::class)

package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.CardSetIcon
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.DieGroup
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.common.DieIcon
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.stevedenheyer.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.stevedenheyer.starwarsdestinydeckbuilder.domain.model.Format
import com.stevedenheyer.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.asIntPair
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.getUniqueInline
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardDetailDeckUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardDetailUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardUiState
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.DetailViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.toDetailUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Integer.parseInt
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    isCompactScreen: Boolean,
    modifier: Modifier = Modifier,
    detailViewModel: DetailViewModel = hiltViewModel(),
    navigateToCard: (String) -> Unit,
    navigateBack: () -> Unit
) {

    val cardState by detailViewModel.uiCard.collectAsStateWithLifecycle(
        initialValue = CardUiState.NoData(
            isLoading = true,
            errorMessage = null
        )
    )

    val decks by detailViewModel.uiDecks.collectAsStateWithLifecycle(initialValue = emptyList())

    val owned by detailViewModel.ownedCardsUi.collectAsStateWithLifecycle(initialValue = 0)

  //  Log.d("SWD", "Card State: ${cardState.isLoading}")
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(topBar = {
        TopAppBar(title = { },
            navigationIcon = {
                IconButton(onClick = { navigateBack() }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
        )
    }) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
        ) {

            when (val state = cardState) {
                is CardUiState.HasData -> Details(
                    isCompactScreen = isCompactScreen,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.primaryContainer),
                    card = state.data,
                    decks = decks,
                    changeCardQuantity = { deckName, quantity, isElite, isSetAside ->
                        (detailViewModel::writeDeck)(
                            deckName,
                            quantity,
                            isElite,
                            isSetAside
                        )
                    },
                    owned = owned,
                    changeOwnedQuantity = { quantity -> (detailViewModel::writeOwned)(quantity) },
                    findCardbySetAndPostition = { set, position ->
                            val card = detailViewModel.getCardBySetAndPosition(set, position)
                            card.code
                    },
                    navigateToCard = navigateToCard
                )


                is CardUiState.NoData -> {
                    if (cardState.errorMessage != null) {
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar(
                                cardState.errorMessage!!,
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    }
                }
            }
            
            if (cardState.isLoading) {
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
fun Details(
    isCompactScreen: Boolean,
    card: CardDetailUi,
    decks: List<CardDetailDeckUi>,
    owned: Int,
    modifier: Modifier = Modifier,
    changeCardQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit,
    changeOwnedQuantity: (quantity: Int) -> Unit,
    findCardbySetAndPostition: suspend (String, Int) -> String?,
    navigateToCard: (String) -> Unit) {
    when (isCompactScreen) {
        true -> CompactDetails(
            card = card,
            decks = decks,
            owned = owned,
            changeCardQuantity = changeCardQuantity,
            changeOwnedQuantity = changeOwnedQuantity,
            findCardbySetAndPostition = findCardbySetAndPostition,
            navigateToCard = navigateToCard,
            modifier = modifier
        )

        false -> LargeDetails(
            card = card,
            decks = decks,
            owned = owned,
            changeCardQuantity = changeCardQuantity,
            changeOwnedQuantity = changeOwnedQuantity,
            navigateToCard = navigateToCard,
            findCardbySetAndPostition = findCardbySetAndPostition,
            modifier = modifier
        )
    }
}
        
@Composable
fun CompactDetails(
    card: CardDetailUi,
    decks: List<CardDetailDeckUi>,
    owned: Int,
    modifier: Modifier = Modifier,
    changeCardQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit,
    changeOwnedQuantity: (quantity: Int) -> Unit,
    findCardbySetAndPostition: suspend (String, Int) -> String?,
    navigateToCard: (String) -> Unit,
) {
    LazyColumn(modifier) {
        item {
            DetailsCard(modifier = Modifier.padding(vertical = 8.dp),
                card = card,
                findCardbySetAndPostition = findCardbySetAndPostition,
                navigateToCard = navigateToCard)
        }
        item {
            OwnedCard(modifier = Modifier.padding(vertical = 8.dp), owned = owned, changeQuantity = changeOwnedQuantity)
        }
        items(items = decks, key = { it.name }) {deck ->
            DeckCardCompact(
                modifier = Modifier.padding(vertical = 8.dp),
                deck = deck,
                card = card,
            ) { deckName, quantity, isElite, isSetAside -> changeCardQuantity(deckName, quantity, isElite, isSetAside) }
        }
        item {
            ImageCard(modifier = Modifier, src = card.imageSrc)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun LargeDetails(
    card: CardDetailUi,
    decks: List<CardDetailDeckUi>,
    owned: Int,
    modifier: Modifier = Modifier,
    changeCardQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit,
    changeOwnedQuantity: (quantity: Int) -> Unit,
    findCardbySetAndPostition: suspend (String, Int) -> String?,
    navigateToCard: (String) -> Unit,
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
                DetailsCard(modifier = Modifier.weight(1f), card = card, findCardbySetAndPostition = findCardbySetAndPostition, navigateToCard = navigateToCard)
                GlideImage(
                    model = card.imageSrc,
                    contentDescription = "Card Picture",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .weight(0.8f)
                        .padding(horizontal = 2.dp)
                )
            }
        }
        item {
            OwnedCard(modifier = Modifier.padding(vertical = 8.dp), owned = owned, changeQuantity = changeOwnedQuantity)
        }
        items(items = decks, key = { it.name }) { deck ->
            DeckCard(
                modifier = Modifier.fillMaxWidth(),
                deck = deck,
                card = card
            ) { deckName, quantity, isElite, isSetAside -> changeCardQuantity(deckName, quantity, isElite, isSetAside) }
        }
    }
}

@Composable
fun DetailsCard(modifier: Modifier, card: CardDetailUi, findCardbySetAndPostition: suspend (String, Int) -> String?, navigateToCard: (String) -> Unit) {

    val cardColor = getColorFromString(card.color)

    val textModifer = Modifier
        .padding(start = 8.dp)
        .padding(vertical = 2.dp)

    val scope = rememberCoroutineScope()

    val inlineContent = HashMap<String, InlineTextContent>().apply {

        this.putAll(DieIcon.entries.associate { die ->
            Pair(die.inlineTag, InlineTextContent(
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
        })

        this.putAll(CardSetIcon.entries.associate { set ->
            Pair(set.code, InlineTextContent(
                Placeholder(
                    width = MaterialTheme.typography.bodyLarge.fontSize,
                    height = MaterialTheme.typography.bodyLarge.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
                )
            ) {
                Image(
                    painter = painterResource(id = set.resourceId),
                    contentDescription = set.name,
                    modifier = Modifier.fillMaxSize(),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            })
        })
    }.toMap()

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
                if (card.isUnique && (card.typeName == "Character" || card.typeName == "Plot")) appendInlineContent("unique", "unique")
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
            inlineContent = getUniqueInline(size = MaterialTheme.typography.titleLarge.fontSize, color = MaterialTheme.colorScheme.onSurface ),
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
                        append(" - $it")
                    }
                    append(". ")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer
        )

        Spacer(modifier = Modifier.size(10.dp))

        val pointCostLabel = if (card.cost != null) "Cost" else "Points"
        val pointsOrCost = card.cost ?: card.points

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

        Spacer(modifier = Modifier.size(10.dp))

        if (card.sides != null) {
            DieGroup(
                modifier = Modifier
                    .padding(vertical = 6.dp, horizontal = 8.dp)
                    .fillMaxWidth()
                    .wrapContentWidth(align = Alignment.CenterHorizontally)
                    .height(35.dp),
                dieCodes = card.sides
            )
        }

        Spacer(modifier = Modifier.size(20.dp))

        if (card.hasErrata) {
            Text(
                "This card was errata'd",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                modifier = textModifer
            )
        }

        val layoutResult = remember {
            mutableStateOf<TextLayoutResult?>(null)
        }

        val cardText = parseHtml(card.text ?: "", inlineContent)
        Text(
            text = cardText,
            style = MaterialTheme.typography.bodyLarge,
            inlineContent = inlineContent,
            onTextLayout = { layoutResult.value = it },
            modifier = textModifer.pointerInput(Unit) {
                    detectTapGestures { offset ->
                        layoutResult.value?.let { layoutResult ->
                            val position = layoutResult.getOffsetForPosition(offset)
                            cardText.getStringAnnotations(
                                start = position - 1,
                                end = position + 3
                            ).firstOrNull { annotation ->
                                annotation.tag == "LINK"
                            }?.let { annotation ->
                                //navigate to card
                                Log.d("SWD", "Link click deteced")
                               scope.launch(Dispatchers.IO) {
                                   val strings = annotation.item.split("_")
                                   val setCode = strings.first()
                                   try {
                                       val cardPosition = parseInt(strings.last())
                                       Log.d("SWD", "Link click deteced: $setCode, $cardPosition")
                                       val cardCode = findCardbySetAndPostition(setCode, cardPosition)
                                       Log.d("SWD", "Card:  $cardCode")
                                       withContext(Dispatchers.Main) {
                                           if (cardCode != null)
                                               navigateToCard(cardCode)
                                       }
                                   } catch (e: NumberFormatException) {
                                       Log.wtf("SWD", "No integer found in Link Annotation")
                                   }
                               }

                            }

                        }
                    }
                },
        )

        Spacer(modifier = Modifier.size(5.dp))

        Text(
            parseHtml(card.flavor ?: "", inlineContent),
            style = MaterialTheme.typography.bodyMedium,
            modifier = textModifer,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.size(10.dp))

        if (!card.illustrator.isNullOrEmpty()) {
            Row(modifier = textModifer) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_brush_24),
                    contentDescription = "Paintbrush Icon",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(" " + card.illustrator, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.size(10.dp))

        Text(
            buildAnnotatedString {
              appendInlineContent(card.setCode, " ")
              append(" ${card.setName} #${card.position}")
            },
           // "${card.setName} #${card.position}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = textModifer,
            inlineContent = inlineContent
        )

        if (card.reprints.isNotEmpty()) {
            Text(
                "Reprinted in:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifer
            )
            card.reprints.forEach {
                val s = buildAnnotatedString {
                    pushStringAnnotation(tag = "LINK", annotation = it.code)
                    withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append("${it.setName} #${it.position}")
                    }
                }
                ClickableText(
                    text = s,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = textModifer,
                ) { offset ->
                    s.getStringAnnotations("LINK", offset, offset)
                        .firstOrNull()?.let { annotation ->
                            navigateToCard(annotation.item)
                        }
                }
            }
        }

        if (card.parallelDice.isNotEmpty()) {
            Text(
                "Used as parallel die by:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = textModifer
            )
            card.parallelDice.forEach {
                val s = buildAnnotatedString {
                    pushStringAnnotation(tag = "LINK", annotation = it.code)
                    withStyle(SpanStyle(color = Color.Blue, textDecoration = TextDecoration.Underline)) {
                        append("${it.setName} #${it.position}")
                    }
                }
                ClickableText(
                    text = s,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = textModifer,
                ) { offset ->
                    s.getStringAnnotations("LINK", offset, offset)
                        .firstOrNull()?.let { annotation ->
                            navigateToCard(annotation.item)
                        }

                }
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
   // Log.d("SWD", "Legality: ${formats.size} ${formats}")
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
fun OwnedCard(
    modifier: Modifier,
    owned: Int,
    changeQuantity: (quantity: Int) -> Unit
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
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Quantity owned",
                style = MaterialTheme.typography.titleLarge,
                modifier = textModifer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            AddOwned(modifier = Modifier, deck = owned, changeQuantity = changeQuantity)
        }
    }
}

@Composable
fun DeckCard(
    modifier: Modifier,
    card: CardDetailUi,
    deck: CardDetailDeckUi,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
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
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                deck.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = textModifer,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                buildAnnotatedString {
                    append(deck.formatName)
                    if (card.formats.find { it.gameType == deck.formatName }?.legality == "banned") {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append(" !") }
                    }
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = textModifer
            )
            Text(
                buildAnnotatedString {
                    append(deck.affiliationName)
                    if (card.affiliation != deck.affiliationName && card.affiliation != "Neutral") {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append(" !") }
                    }
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = textModifer
            )
            Text(
                buildAnnotatedString {
                    if (card.formats.find { it.gameType == deck.formatName }?.legality == "banned") {
                        append("Banned. ")
                    }
                    when (card.typeName) {
                        "Character" -> {
                            append("${deck.pointsUsed} points used.")
                        }
                        "Battlefield" -> if (deck.battlefield != card.code && deck
                                .battlefield != null) append("Deck has Battlefield - will replace.")
                        "Plot" -> if (deck.plot != card.code && deck
                                .plot != null) append("Deck has Plot - will replace.")
                        else -> append("Deck size ${deck.deckSize} cards.")
                    }
                },
                style = MaterialTheme.typography.titleSmall,
                modifier = textModifer
            )
            DeckControls(
                modifier = Modifier,
                deck = deck,
                card = card,
                changeQuantity = changeQuantity
            )
        }
    }
}

@Composable
fun DeckCardCompact(
    modifier: Modifier,
    deck: CardDetailDeckUi,
    card: CardDetailUi,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
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
        /*Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) */
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
        )
        {
           // Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier) {
                Text(
                    deck.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = textModifer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(
                            buildAnnotatedString {
                                if (card.formats.find { it.gameType == deck.formatName }?.legality == "banned") {
                                    append("Banned. ")
                                }
                                when (card.typeName) {
                                    "Character" -> {
                                        append("${deck.pointsUsed} points used.")
                                    }

                                    "Battlefield" -> if (deck.battlefield != card.code && deck
                                            .battlefield != null
                                    ) append("Deck has Battlefield - will replace.")

                                    "Plot" -> {
                                        if (deck.plot != card.code && deck
                                                .plot != null
                                        ) append("Deck has Plot - will replace.")
                                        append("${deck.pointsUsed} points used.")
                                    }

                                    else -> append("Deck size ${deck.deckSize} cards.")
                                }
                            },
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            modifier = textModifer.widthIn(max = 200.dp)
                        )
                        Row(horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(
                                buildAnnotatedString {
                                    append(deck.formatName)
                                    if (card.formats.find { it.gameType == deck.formatName }?.legality == "banned") {
                                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                            append(
                                                " !"
                                            )
                                        }
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = textModifer.width(120.dp)
                            )
                            Text(
                                buildAnnotatedString {
                                    append(deck.affiliationName)
                                    if (card.affiliation != deck.affiliationName && card.affiliation != "Neutral") {
                                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                            append(
                                                " !"
                                            )
                                        }
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                modifier = textModifer
                            )
                        }
                    }
                    //   }
                    // }
                    DeckControls(
                        modifier = Modifier,
                        deck = deck,
                        card = card,
                        changeQuantity = changeQuantity
                    )
                }
            }
        }
   // }
}

@Composable
fun DeckControls(
    modifier: Modifier,
    card: CardDetailUi,
    deck: CardDetailDeckUi,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
) =
    when (card.typeName) {
        "Battlefield" -> AddSingle(
            modifier = modifier,
            deck = deck,
            deckHasCard = card.code == deck.battlefield,
            changeQuantity = changeQuantity
        )

        "Plot" -> {
            if (card.points.asIntPair().second == null)
            AddSingle(
                modifier = modifier,
                deck = deck,
                deckHasCard = card.code == deck.plot,
                changeQuantity = changeQuantity
            )
            else
                AddElitable(modifier = modifier, deck = deck, changeQuantity = changeQuantity)
        }

        "Character" -> {
            if (deck.isUnique)
                AddElitable(modifier = modifier, deck = deck, changeQuantity = changeQuantity)
            else
                AddMultiple(modifier = modifier, deck = deck, changeQuantity = changeQuantity)
        }

        else -> AddMultiple(modifier, deck, changeQuantity)

        }


@Composable
fun AddElitable(
    modifier: Modifier,
    deck: CardDetailDeckUi,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
) {
    TextButton(border = BorderStroke(
        2.dp,
        color = MaterialTheme.colorScheme.primary,

        ),
        modifier = modifier.width(140.dp),
        onClick = {
            when (deck.quantity) {
                0 -> changeQuantity(deck.name, 1, false, false)
                1 -> changeQuantity(deck.name, 2, true, false)
                2 -> changeQuantity(deck.name, 3, false, true)
                3 -> changeQuantity(deck.name, 0, false, false)
            }
        }) {
        Text(
            buildAnnotatedString {
                withStyle(ParagraphStyle(textAlign = TextAlign.Center)) {
                    when (deck.quantity) {
                        0 -> append("Add")
                        1 -> append("Make Elite")
                        2 -> append("Make\nSet-Aside")
                        3 -> append("Remove")
                    }
                }
            },
            Modifier
                .wrapContentSize(align = Alignment.Center),
            softWrap = true,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun AddSingle(
    modifier: Modifier,
    deck: CardDetailDeckUi,
    deckHasCard: Boolean,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
) {
    TextButton(border = BorderStroke(
        2.dp,
        color = MaterialTheme.colorScheme.primary,

        ),
        modifier = modifier.width(140.dp),
        onClick = {
            if (!deckHasCard) {
                changeQuantity(deck.name, 1, false, false)
            }
            else {
                changeQuantity(deck.name, 0, true, false)
            }
        }) {
        Text(
            buildAnnotatedString {
                if (!deckHasCard) {
                    append("Add")
                }
                else {
                    append("Remove")
                }
            },
            Modifier
                .wrapContentSize(align = Alignment.Center),
            softWrap = true,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
fun AddMultiple(
    modifier: Modifier,
    deck: CardDetailDeckUi,
    changeQuantity: (deckName: String, quantity: Int, isElite: Boolean, isSetAside: Boolean) -> Unit
) {
    Row(modifier = modifier) {
        TextButton(border = BorderStroke(
            2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
            enabled = (deck.quantity > -1),
            onClick = { changeQuantity(deck.name,
                deck.quantity - 1,
                false,
                deck.quantity -1 < 0) }) {
            Text(
                "-",
                Modifier
                    .wrapContentSize(align = Alignment.Center),
                softWrap = true,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            buildAnnotatedString {
                if (deck.quantity in 0..deck.maxQuantity) {
                    withStyle(style = SpanStyle(fontStyle = MaterialTheme.typography.headlineMedium.fontStyle, fontSize = MaterialTheme.typography.headlineMedium.fontSize)) {
                        append(deck.quantity.toString())
                    }
                } else {
                    withStyle(style = SpanStyle(fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
                        baselineShift = BaselineShift.Subscript)) {
                        withStyle(style = ParagraphStyle(textAlign = TextAlign.Center)) {
                            append("Set-\nAside")
                        }
                    }
                }
            },

            modifier = Modifier.padding(horizontal = 12.dp).align(Alignment.CenterVertically)
        )
        TextButton(border = BorderStroke(
            2.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
            enabled = (deck.quantity < deck.maxQuantity + 1),
            onClick = { changeQuantity(deck.name,
                deck.quantity + 1,
                false,
                deck.quantity + 1 > deck.maxQuantity) }) {
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

@Composable
fun AddOwned(
    modifier: Modifier,
    deck: Int,
    changeQuantity: (quantity: Int) -> Unit
) {
    Row(modifier = modifier) {
        TextButton(border = BorderStroke(
            2.dp,
            color = MaterialTheme.colorScheme.primary
        ),
            enabled = (deck > 0),
            onClick = { changeQuantity(
                deck - 1,
               ) }) {
            Text(
                "-",
                Modifier
                    .wrapContentSize(align = Alignment.Center),
                softWrap = true,
                style = MaterialTheme.typography.titleLarge,
            )
        }
        Text(
            buildAnnotatedString {
                    withStyle(style = SpanStyle(fontStyle = MaterialTheme.typography.headlineMedium.fontStyle, fontSize = MaterialTheme.typography.headlineMedium.fontSize)) {
                        append(deck.toString())
                    }
            },

            modifier = Modifier.padding(horizontal = 12.dp).align(Alignment.CenterVertically)
        )
        TextButton(border = BorderStroke(
            2.dp,
            color = MaterialTheme.colorScheme.primary,
        ),
          //  enabled = (deck < deck.maxQuantity),
            onClick = { changeQuantity(
                deck + 1,
               ) }) {
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

fun parseHtml(s: String, inlines: Map<String, InlineTextContent> = emptyMap()): AnnotatedString {
    val strings = s.split("<", ">", "[", "]").listIterator()
    return buildAnnotatedString {
        while (strings.hasNext()) {
            when (val string = strings.next()) {
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

                "cite" -> withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                    append("\n\t\t\t\t\t-${strings.next()}")
                }

                "/cite" -> {}

                in inlines.keys -> {
                    if (string in CardSetIcon.entries.map { it.code }) {
                        val code = string
                        val nextStrings = strings.next().split(")")
                        try {
                            val cardNum = parseInt(nextStrings.first())
                            pushStringAnnotation(tag = "LINK", annotation = code + "_" + cardNum)
                            withStyle(
                                SpanStyle(
                                    color = Color.Blue,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                appendInlineContent(string, "[" + string + "]")
                                append("$cardNum")
                            }
                            append(")")
                            pop()

                            nextStrings.drop(1).forEachIndexed { index, s ->
                                append(s)
                                if (index < nextStrings.drop(1).size - 1) {
                                    append(")")
                                }
                            }
                        } catch (e: NumberFormatException) {
                            Log.d("SWD", "Couldn't find an integer for set position")
                            nextStrings.forEach {
                                append(it)
                            }
                        }
                    } else {
                        appendInlineContent(string, "[" + string + "]")
                    }
                }
                else -> append(string)
            }
        }
    }
}

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
            .fillMaxSize(), card = CardDTO.testCard.toDomain().toDetailUi(),
        findCardbySetAndPostition = { _, _ -> "" }
    ) {}
}

val testCard = CardDetailUi(
    name = "test",
    typeName = "Support",
    affiliation = "Hero",
    code = "00000",
    color = "Red",
    cost = "8",
    parallelDice = emptyList(),
    position = 0,
    deckLimit = 1,
    faction = "",
    formats = listOf(Format(gameType = "Standard", legality = "banned", balance = "11/10")),
    rarity = "",
    subtitle = null,
    subtypes = emptyList(),
    points = "8/11",
    flavor = null,
    hasErrata = false,
    health = 8,
    illustrator = null,
    imageSrc = URL("https://db.swdrenewedhope.com/"),
    isUnique = true,
    reprints = emptyList(),
    sides = emptyList(),
    text = null,
    setName = "",
    setCode = "AW"
)

@Preview(widthDp = 700)
@Composable
fun DecksPreview() {
    val decks = listOf(
        CardDetailDeckUi(
            name = "test",
            affiliationName = "Hero",
            formatName = "Infinite",
            quantity = 0,
            maxQuantity = 2,
            isUnique = true,
            battlefield = null,
            plot = null,
            pointsUsed = 30,
            deckSize = 25,
            isElite = false
        ),
        CardDetailDeckUi(
            name = "Loooooooongname",
            affiliationName = "Villain",
            formatName = "ARH Standard",
            quantity = 0,
            maxQuantity = 2,
            isUnique = false,
            battlefield = null,
            plot = null,
            pointsUsed = 30,
            deckSize = 25,
            isElite = false
        )
    )
    val deck = CardDetailDeckUi(
        name = "test",
        affiliationName = "Hero",
        formatName = "Infinite",
        quantity = 0,
        maxQuantity = 2,
        isUnique = false,
        battlefield = null,
        plot = null,
        pointsUsed = 30,
        deckSize = 25,
        isElite = false
    )
    DeckCard(modifier = Modifier, deck = deck, card = testCard) { name, quan, isElite, isSetAside -> }
}

@Preview(widthDp = 400)
@Composable
fun DecksCompactPreview() {
    val decks = listOf(
        CardDetailDeckUi(
            name = "test",
            affiliationName = "Hero",
            formatName = "Infinite",
            quantity = 0,
            maxQuantity = 2,
            isUnique = false,
            battlefield = null,
            plot = null,
            pointsUsed = 30,
            deckSize = 25,
            isElite = false
        ),
        CardDetailDeckUi(
            name = "Loooooooongname",
            affiliationName = "Villain",
            formatName = "ARH Standard",
            quantity = 0,
            maxQuantity = 2,
            isUnique = true,
            battlefield = null,
            plot = null,
            pointsUsed = 30,
            deckSize = 25,
            isElite = false
        )
    )
    val deck = CardDetailDeckUi(
        name = "test",
        affiliationName = "Hero",
        formatName = "Standard",
        quantity = -1,
        maxQuantity = 2,
        isUnique = true,
        battlefield = "00001",
        plot = null,
        pointsUsed = 30,
        deckSize = 25,
        isElite = false
    )
    DeckCardCompact(modifier = Modifier, deck = deck, card = testCard) { name, quan, isElite, isSetAside -> }
}


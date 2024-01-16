package com.example.starwarsdestinydeckbuilder.compose

import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.starwarsdestinydeckbuilder.R
import com.example.starwarsdestinydeckbuilder.data.remote.mappings.toDomain
import com.example.starwarsdestinydeckbuilder.data.remote.model.CardDTO
import com.example.starwarsdestinydeckbuilder.domain.model.Format
import com.example.starwarsdestinydeckbuilder.ui.theme.getColorFromString
import com.example.starwarsdestinydeckbuilder.viewmodel.CardDetailUi
import com.example.starwarsdestinydeckbuilder.viewmodel.DetailViewModel
import com.example.starwarsdestinydeckbuilder.viewmodel.toDetailUi
import retrofit2.http.Url
import java.net.URL

val mediumText = 16.sp
val smallText = 12.sp

val inlineContent = DieIcon.entries.associate { die ->
    Pair(die.code, InlineTextContent(
        Placeholder(
            width = mediumText,
            height = mediumText,
            placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline
        )
    ) {
        Image(
            painter = painterResource(id = die.resourceId),
            contentDescription = die.inlineTag,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(Color.White)
        )
    })
}

@Composable
fun DetailsScreen(isCompactScreen: Boolean,
                    modifier:Modifier = Modifier,
                  detailViewModel: DetailViewModel = hiltViewModel()) {

    val card by detailViewModel.card.collectAsStateWithLifecycle(initialValue = null)

    if (card != null) {
        if (isCompactScreen) {
            val scrollState = rememberScrollState()
            Column(modifier.verticalScroll(state = scrollState)) {
                CardText(modifier = Modifier.padding(vertical = 8.dp), card = card!!)
                ImageCard(modifier = Modifier, src = card!!.imagesrc)
            }
        } else {
            Row(modifier = modifier.fillMaxSize()) {
                CardText(modifier = Modifier.weight(1F), card = card!!)
                ImageCard(modifier = Modifier.weight(0.8F), src = card!!.imagesrc)
            }
        }
    }
}

@Composable
fun CardText(modifier: Modifier, card: CardDetailUi) {

    val cardColor = getColorFromString(card.color)
    val textModifer = Modifier
        .padding(start = 8.dp)
        .padding(vertical = 2.dp)

    OutlinedCard(modifier = modifier,
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                border = BorderStroke(4.dp, color = cardColor)
    ) {

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Black)) {
                    withStyle(style = SpanStyle(fontSize = 24.sp, fontWeight = FontWeight(550))) {
                        append(card.name)
                    }
                    if (!card.subtitle.isNullOrBlank())
                        withStyle(style = SpanStyle(fontSize = 16.sp)) {
                            append(" - ${card.subtitle}")
                        }
                }
        },
            modifier = Modifier
                .background(color = cardColor)
                .padding(start = 12.dp, top = 12.dp, bottom = 8.dp)
                .fillMaxWidth())

        Text("${card?.affiliation}. ${card?.faction}. ${card?.rarity}", fontSize = mediumText, modifier = textModifer)

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = mediumText)) {
                    withStyle(SpanStyle(fontWeight = FontWeight(550))) {
                    append(card.typeName)
                    card.subtypes?.forEach {
                        append(" - ${it}")
                    }
                        append(". ")
                    }
                    append("Points: ${card.points}.  Health: ${card.health}")
                }
            },
            modifier = textModifer
        )

        if (card.sides != null) {
            DieGroup(
                modifier = textModifer
                    .padding(vertical = 6.dp)
                    .fillMaxWidth(0.8f)
                    .height(30.dp),
                dieCodes = card.sides
            )
        }

        if (card.has_errata) {
            Text("This card was errata'd", fontSize = smallText, modifier = textModifer)
        }
        Text(parseHtml(card.text ?: ""), fontSize = mediumText, modifier = textModifer, inlineContent = inlineContent)
        Text(card.flavor ?: "", fontSize = smallText, modifier = textModifer, fontStyle = FontStyle.Italic)
        if (!card.illustrator.isNullOrEmpty()) {
            Row (modifier = textModifer) {
                Image(painter = painterResource(id = R.drawable.baseline_brush_24), contentDescription = "Paintbrush Icon", modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(Color.White) )
                Text(card.illustrator, fontSize = mediumText)
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        Text("${card.setName} #${card.position}", fontSize = mediumText, modifier = textModifer)

        if (card.reprints.isNotEmpty()) {
            Text("Reprinted in:", fontSize = smallText, modifier = textModifer)
            card.reprints.forEach {
                Text("${it.setName} #${it.position}", fontSize = mediumText, modifier = textModifer, color = Color.Blue)
            }
        }

        if (card.parellelDice.isNotEmpty()) {
            Text("Used as parallel die by:")
            card.parellelDice.forEach {
                Text("${it.setName} #${it.position}", fontSize = smallText, modifier = textModifer)
            }
        }

        if (card.formats.isNotEmpty()) {
            Legality(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                formats = card.formats
            )
            if (!card.formats.all { format -> format.balance.isNullOrBlank() }) {
                Balance(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                    formats = card.formats
                )
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ImageCard(modifier: Modifier, src: URL) {
    GlideImage(model = src, contentDescription = "", contentScale = ContentScale.FillWidth, alignment = Alignment.TopCenter, modifier = modifier.padding(horizontal = 6.dp))
}

@Composable
fun Legality(modifier: Modifier, formats: List<Format>) {
    val legalityModifier = Modifier
        .border(BorderStroke(width = 0.5.dp, color = Color.White))
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .fillMaxWidth()
        .wrapContentSize(align = Alignment.Center, unbounded = false)

    Column {
    Text("Legality", fontSize = mediumText, modifier = Modifier
        .fillMaxWidth()
        .wrapContentWidth(align = Alignment.CenterHorizontally))

    LazyVerticalGrid(columns = GridCells.Fixed(formats.size), userScrollEnabled = false, modifier = modifier
        .heightIn(max = 600.dp)
        .fillMaxWidth()
        .padding(vertical = 2.dp)
       // .wrapContentSize(align = Alignment.TopCenter, unbounded = false)
        .border(width = 1.dp, color = Color.White)) {
        items(items = formats) { format ->
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    format.gameType, fontSize = smallText,
                    modifier = legalityModifier
                )
                if (format.legality == "banned")
                    Image(
                        painter = painterResource(id = R.drawable.baseline_cancel_24),
                        contentDescription = "Banned",
                        modifier = legalityModifier,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                else
                    Image(
                        painter = painterResource(id = R.drawable.baseline_check_circle_24),
                        contentDescription = "Allowed",
                        modifier = legalityModifier,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
            }
        }
    }
    }
}

@Composable
fun Balance(modifier: Modifier, formats: List<Format>) {
    val balanceModifier = Modifier
        .border(BorderStroke(width = 0.5.dp, color = Color.White))
        .padding(horizontal = 12.dp, vertical = 6.dp)
        .fillMaxWidth()
        .wrapContentSize(align = Alignment.Center)

    Column {
        Text("Balance of the Force", fontSize = mediumText, modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(align = Alignment.CenterHorizontally))

        LazyVerticalGrid(columns = GridCells.Fixed(formats.size), userScrollEnabled = false, modifier = modifier
            .heightIn(max = 600.dp)
            .padding(vertical = 2.dp)
            .wrapContentSize(align = Alignment.TopCenter, unbounded = false)
            .border(width = 1.dp, color = Color.White)) {
            items(items = formats) { format ->
                Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        format.gameType, fontSize = smallText,
                        modifier = balanceModifier
                    )
                    if (format.legality == "banned")
                        Text("--", fontSize = mediumText, modifier = balanceModifier)
                    else
                        Text(format.balance ?: "--", fontSize = mediumText, modifier = balanceModifier)
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
                DieIcon.BLANK.inlineTag -> appendInlineContent(DieIcon.BLANK.code, "[" + DieIcon.BLANK.inlineTag + "]")
                DieIcon.DISCARD.inlineTag -> appendInlineContent(DieIcon.DISCARD.code, "[" + DieIcon.DISCARD.inlineTag + "]")
                DieIcon.DISRUPT.inlineTag -> appendInlineContent(DieIcon.DISRUPT.code, "[" + DieIcon.DISRUPT.inlineTag + "]")
                DieIcon.FOCUS.inlineTag -> appendInlineContent(DieIcon.FOCUS.code, "[" + DieIcon.FOCUS.inlineTag + "]")
                DieIcon.INDIRECT.inlineTag -> appendInlineContent(DieIcon.INDIRECT.code, "[" + DieIcon.INDIRECT.inlineTag + "]")
                DieIcon.MELEE.inlineTag -> appendInlineContent(DieIcon.MELEE.code, "[" + DieIcon.MELEE.inlineTag + "]")
                DieIcon.RANGED.inlineTag -> appendInlineContent(DieIcon.RANGED.code, "[" + DieIcon.RANGED.inlineTag + "]")
                DieIcon.RESOURCE.inlineTag -> appendInlineContent(DieIcon.RESOURCE.code, "[" + DieIcon.RESOURCE.inlineTag + "]")
                DieIcon.SHIELD.inlineTag -> appendInlineContent(DieIcon.SHIELD.code, "[" + DieIcon.SHIELD.inlineTag + "]")
                DieIcon.SPECIAL.inlineTag -> appendInlineContent(DieIcon.SPECIAL.code, "[" + DieIcon.SPECIAL.inlineTag + "]")
                else -> append(string)
            }
        }
    }
}

@Preview
@Composable
fun HtmlParser() {
    val t = parseHtml("Blue Character only.\n<b>Action</b> - Remove this die to turn a die to a side showing a blank ([blank]).")
    Text(t, inlineContent=inlineContent)

}

@Preview
@Composable
fun FormatPreview() {
    val formats = listOf(Format(gameType = "Standard", legality = "banned", balance = "12/16"), Format(gameType = "Infinite", legality = "", balance = "12/16"))
    Balance(modifier = Modifier.fillMaxSize(), formats = formats)
}

@Preview
@Composable
fun TextCardPreview() {
    CardText(modifier = Modifier
        .background(Color.Black)
        .fillMaxSize(), card = CardDTO.testCard.toDomain().toDetailUi())
}


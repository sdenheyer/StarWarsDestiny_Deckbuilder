package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi
import com.stevedenheyer.starwarsdestinydeckbuilder.utils.arrowInline

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CollectionList(
    isCompactScreen: Boolean,
    cards: List<CardUi>,
    modifier: Modifier,
    onItemClick: (String) -> Unit,
    onRefreshSwipe: () -> Unit
) {

    val refreshing by remember { mutableStateOf(false) }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = { onRefreshSwipe() },
        refreshThreshold = 60.dp
    )

    var characterExpanded by remember { mutableStateOf(true) }

    var battlefieldExpanded by remember { mutableStateOf(true) }

    var plotExpanded by remember { mutableStateOf(true) }

    var upgradeExpanded by remember { mutableStateOf(true) }

    var downgradeExpanded by remember { mutableStateOf(true) }

    var supportExpanded by remember { mutableStateOf(true) }

    var eventExpanded by remember { mutableStateOf(true) }

    Box {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {

            item {
                Text(
                    buildAnnotatedString {
                        if (!characterExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Character")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { characterExpanded = !characterExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (characterExpanded)
                items(items = cards.filter { it.type == "Character" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!battlefieldExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Battlefield")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { battlefieldExpanded = !battlefieldExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (battlefieldExpanded)
                items(
                    items = cards.filter { it.type == "Battlefield" },
                    key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!plotExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Plot")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { plotExpanded = !plotExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (plotExpanded)
                items(items = cards.filter { it.type == "Plot" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!upgradeExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Upgrade")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { upgradeExpanded = !upgradeExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (upgradeExpanded)
                items(items = cards.filter { it.type == "Upgrade" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!downgradeExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Downgrade")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { downgradeExpanded = !downgradeExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (downgradeExpanded)
                items(items = cards.filter { it.type == "Downgrade" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!supportExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Support")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { supportExpanded = !supportExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (supportExpanded)
                items(items = cards.filter { it.type == "Support" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }

            item {
                Text(
                    buildAnnotatedString {
                        if (!eventExpanded) appendInlineContent(
                            "collapsedArrow",
                            "collapsed"
                        ) else appendInlineContent("dropDownArrow", "expanded")
                        append("Event")
                    },
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .clickable { eventExpanded = !eventExpanded }
                        .padding(bottom = 4.dp),
                    inlineContent = arrowInline
                )
            }
            if (eventExpanded)
                items(items = cards.filter { it.type == "Event" }, key = { it.code }) { card ->
                    CardItem(
                        isScreenCompact = isCompactScreen,
                        modifier = Modifier,
                        card = card,
                        onItemClick = onItemClick
                    )
                }
        }
    }
}
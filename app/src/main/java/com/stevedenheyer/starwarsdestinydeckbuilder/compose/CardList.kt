package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.CardUi

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CardList(isCompactScreen: Boolean, cards: List<CardUi>, scrollState: LazyListState, modifier: Modifier, onItemClick: (String) -> Unit, onRefreshSwipe:() -> Unit) {

        val refreshing by remember { mutableStateOf(false) }

        val pullRefreshState = rememberPullRefreshState(refreshing = refreshing, onRefresh = { onRefreshSwipe() }, refreshThreshold = 60.dp)

        LazyColumn(state = scrollState,
            modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)) {
            items(items = cards, key = { it.code }) { card ->
                    CardItem(isScreenCompact = isCompactScreen, modifier = Modifier, card = card, onItemClick = onItemClick)
            }

    }
}
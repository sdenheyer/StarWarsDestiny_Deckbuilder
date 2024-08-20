package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopBar(
    gameTypes: List<String>,
    sortState: SortUi,
    openQuery: () -> Unit,
    openDrawer: () -> Unit,
    changeSortState: (SortState, String) -> Unit,
) {

    val popupXoffset = remember { mutableIntStateOf(0) }

    val sortMenuExpanded = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.onGloballyPositioned {
                                                 popupXoffset.intValue = it.size.height
        },
        title = {
        },
        navigationIcon = {
            IconButton(onClick = {
                openDrawer()
            }) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
               /* Image(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )*/
            }
        },
        actions = {
            IconButton(onClick = { openQuery() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Query",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Box {
            IconButton(onClick = { sortMenuExpanded.value = true }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_sort_24),
                    contentDescription = "Sort",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            DropdownMenu(
                expanded = sortMenuExpanded.value,
                onDismissRequest = { sortMenuExpanded.value = false },
                offset = DpOffset(x = 0.dp, y = 8.dp),
                modifier = Modifier.background(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(8.dp)
                )
            ) {

                val menuItemColors = MenuDefaults.itemColors(
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(Modifier
                        .weight(1f)
                        .padding(end = 2.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("All") },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.GAME_TYPE, "") },
                            enabled = (sortState.gameType != "All" || sortState.gameType.isBlank()),
                            modifier = if (sortState.gameType == "All" || sortState.gameType.isBlank()) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                        for (s in gameTypes)
                        DropdownMenuItem(
                            text = { Text(s) },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.GAME_TYPE, s) },
                            enabled = (sortState.gameType != s),
                            modifier = if (sortState.gameType == s) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        DropdownMenuItem(
                            text = { Text("Sort by Name") },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.NAME, "") },
                            enabled = (sortState.sortState != SortState.NAME),
                            modifier = if (sortState.sortState == SortState.NAME) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Set") },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.SET, "") },
                            enabled = (sortState.sortState != SortState.SET),
                            modifier = if (sortState.sortState == SortState.SET) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Faction") },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.FACTION, "") },
                            enabled = (sortState.sortState != SortState.FACTION),
                            modifier = if (sortState.sortState == SortState.FACTION) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by Points/Cost") },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.POINTS_COST, "") },
                            enabled = (sortState.sortState != SortState.POINTS_COST),
                            modifier = if (sortState.sortState == SortState.POINTS_COST) Modifier.background(
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) else Modifier
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSecondary, thickness = 2.dp, modifier = Modifier.padding(horizontal = 4.dp))
                        DropdownMenuItem(text = {
                            Text(buildAnnotatedString {
                                if (sortState.hideHero)
                                    append("Show")
                                else
                                    append("Hide")
                                append(" Heroes")
                            })
                        },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.HIDE_HERO, "") })
                        DropdownMenuItem(text = {
                            Text(buildAnnotatedString {
                                if (sortState.hideVillain)
                                    append("Show")
                                else
                                    append("Hide")
                                append(" Villains")
                            })
                        },
                            colors = menuItemColors,
                            onClick = { changeSortState(SortState.HIDE_VILLAIN, "") })
                    }
                }
            }
            }
        }
    )
}



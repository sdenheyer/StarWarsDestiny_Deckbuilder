package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.QueryUi
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortState
import com.stevedenheyer.starwarsdestinydeckbuilder.compose.model.SortUi
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.UiCardSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryTopBar(
    sets: List<UiCardSet>,
    sortState: SortUi,
    submitQuery: (QueryUi) -> Unit,
    openDrawer: () -> Unit,
    changeSortState: (SortState) -> Unit,
) {

  //  val keyboardController = LocalSoftwareKeyboardController.current
 //   val focusManager = LocalFocusManager.current

    val popupXoffset = remember { mutableStateOf(0) }

    val sortMenuExpanded = remember { mutableStateOf(false) }

    val queryMenuExpaneded = remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.onGloballyPositioned {
                                                 popupXoffset.value = it.size.height
        },
        title = {
        },
        navigationIcon = {
            IconButton(onClick = {
              //  keyboardController?.hide()
             //   focusManager.clearFocus()
                openDrawer()
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        },
        actions = {
            IconButton(onClick = { queryMenuExpaneded.value = true }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_search_24),
                    contentDescription = "Query",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }

            if (queryMenuExpaneded.value) {
                QueryPopup(sets = sets, popupYoffset = popupXoffset.value, onDismiss = { queryMenuExpaneded.value = false }, submitQuery = { submitQuery(it) })
            }

            IconButton(onClick = { sortMenuExpanded.value = true }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_sort_24),
                    contentDescription = "Sort",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
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

                val menuItemColors = MenuItemColors(
                    textColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    leadingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

                DropdownMenuItem(
                    text = { Text("Sort by Name") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.NAME) },
                    enabled = (sortState.sortState != SortState.NAME),
                    modifier = if (sortState.sortState == SortState.NAME) Modifier.background(color = MaterialTheme.colorScheme.tertiaryContainer) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Set") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SET) },
                    enabled = (sortState.sortState != SortState.SET),
                    modifier = if (sortState.sortState == SortState.SET) Modifier.background(color = MaterialTheme.colorScheme.tertiaryContainer) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Faction") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.FACTION) },
                    enabled = (sortState.sortState != SortState.FACTION),
                    modifier = if (sortState.sortState != SortState.FACTION) Modifier.background(color = MaterialTheme.colorScheme.tertiary) else Modifier
                )
                DropdownMenuItem(
                    text = { Text("Sort by Points/Cost") },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.POINTS_COST) },
                    enabled = (sortState.sortState != SortState.POINTS_COST),
                    modifier = if (sortState.sortState != SortState.POINTS_COST) Modifier.background(color = MaterialTheme.colorScheme.tertiary) else Modifier
                )
                DropdownMenuItem(text = {
                    Text(buildAnnotatedString {
                        if (sortState.showHero)
                            append("Hide")
                        else
                            append("Show")
                        append(" Heroes")
                    })
                },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SHOW_HERO) })
                DropdownMenuItem(text = {
                    Text(buildAnnotatedString {
                        if (sortState.showVillain)
                            append("Hide")
                        else
                            append("Show")
                        append(" Villains")
                    })
                },
                    colors = menuItemColors,
                    onClick = { changeSortState(SortState.SHOW_VILLAIN) })
            }
        }
    )
}



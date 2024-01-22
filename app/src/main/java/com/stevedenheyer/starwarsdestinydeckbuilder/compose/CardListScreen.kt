package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListUiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(isCompactScreen: Boolean,
                   modifier: Modifier = Modifier,
                   cardVM: CardViewModel = hiltViewModel(),
                   onCardClick: (String) -> Unit,
) {

  //  Log.d("SWD", "Recomposing...")
    val listUiState by cardVM.cardsFlow.collectAsStateWithLifecycle(ListUiState.noData(isLoading = true, errorMessage = null))
    val set by cardVM.cardSetMenuItemsState.collectAsStateWithLifecycle(initialValue = emptyList())
    //var expanded by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()


    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet {
                LazyColumn {
                    items(items = set ?: emptyList(), key = { it.code }) { item ->
                        NavigationDrawerItem(label = { Text(item.name) }, selected = false, onClick = {
                            (cardVM::setCardSetSelection)(item.code)
                            scope.launch { drawerState.close() }
                            })

                    }
                }

                }
            })
         {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    title = {
                        Text(
                            "Star Wars: Destiny",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.apply { if (isClosed) open() else close() } }
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_menu_24),
                                contentDescription = "Menu",
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            )
                        }
                    }
                )
            },
        ) { padding ->
                /*  Box(
                modifier = modifier
                    .wrapContentSize(Alignment.TopStart)
                    .padding(padding)
            ) {
                Button(onClick = { expanded = true }) {
                    Text(text = "dropdown")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    set?.forEach { set ->
                        DropdownMenuItem(text = { Text(set.name) }, onClick = {
                            Log.d("SWD", "Sending button response...")
                            (cardVM::setCardSetSelection)(set.code)
                            expanded = false })
                    }
                }
            }*/

                when (val state = listUiState) {
                    is ListUiState.hasData -> TextList(
                        isCompactScreen,
                        cards = state.cards,
                        modifier = modifier.padding(padding),
                        onCardClick
                    )

                    is ListUiState.noData -> {
                        if (state.isLoading) {
                            Text(
                                text = "Loading...", fontSize = 48.sp, modifier = modifier
                                    .fillMaxSize()
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        } else {
                            Text(
                                text = state.errorMessage ?: "",
                                fontSize = 48.sp,
                                modifier = modifier
                                    .fillMaxSize()
                                    .wrapContentSize(align = Alignment.Center)
                            )
                        }

                    }
                }
            }
    }
}

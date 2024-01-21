package com.stevedenheyer.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.CardViewModel
import com.stevedenheyer.starwarsdestinydeckbuilder.viewmodel.ListUiState

@Composable
fun CardListScreen(isCompactScreen: Boolean,
                   modifier: Modifier = Modifier,
                   cardVM: CardViewModel = hiltViewModel(),
                   onCardClick: (String) -> Unit,
) {

  //  Log.d("SWD", "Recomposing...")
    val listUiState by cardVM.cardsFlow.collectAsStateWithLifecycle(ListUiState.noData(isLoading = true, errorMessage = null))
    val set by cardVM.cardSetMenuItemsState.collectAsStateWithLifecycle(initialValue = emptyList())
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = modifier
                .wrapContentSize(Alignment.TopStart)
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
        }

        when (val state = listUiState) {
            is ListUiState.hasData -> TextList(isCompactScreen, cards = state.cards, modifier = modifier, onCardClick)
            is ListUiState.noData -> { if (state.isLoading) {
                                            Text(text = "Loading...", fontSize = 48.sp, modifier = modifier.fillMaxSize().wrapContentSize(align = Alignment.Center))
                }
             else {
                 Text(text = state.errorMessage ?: "", fontSize = 48.sp, modifier = modifier.fillMaxSize().wrapContentSize(align = Alignment.Center))
            }

        }
        }
    }
}

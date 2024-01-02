package com.example.starwarsdestinydeckbuilder.compose

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starwarsdestinydeckbuilder.viewmodel.CardViewModel

@Composable
fun CardListScreen(modifier: Modifier = Modifier,
                   cardVM: CardViewModel = hiltViewModel(),
                   onCardClick: (String) -> Unit,
) {

  //  Log.d("SWD", "Recomposing...")
    val cards by cardVM.cardsFlow.collectAsStateWithLifecycle(emptyList())
    val set by cardVM.cardSetMenuItemsState.collectAsStateWithLifecycle(initialValue = emptyList())
    var expanded by remember { mutableStateOf(false) }

    Column {
        Box(
            modifier = Modifier
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
        TextList(cards = cards, modifier = modifier, onCardClick)
    }
}

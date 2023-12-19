package com.example.starwarsdestinydeckbuilder

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.starwarsdestinydeckbuilder.ui.theme.StarWarsDestinyDeckbuilderTheme
import com.example.starwarsdestinydeckbuilder.viewmodel.CardViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StarWarsDestinyDeckbuilderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier,
                cardVM: CardViewModel = hiltViewModel()) {

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
                    DropdownMenuItem(text = { Text(set.name) }, onClick = {(cardVM::setCardSetSelection)(set.code)})
                }
            }
            //Text(set.toString())
        }
        Text("Test")
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = cards, key = { it.code }) { card ->
                Log.d("SWD", "Recomposing: $card")
                Row(modifier = Modifier.fillMaxSize()) {
                    Text(card.name)
                    Text(card.affiliation)
                    Text(card.faction)
                    Text(card.points)
                    Text("${card.health ?: ""}")
                    Text(card.type)
                    Text(card.die1)
                    Text(card.die2)
                    Text(card.die3)
                    Text(card.die4)
                    Text(card.die5)
                    Text(card.die6)
                    Text(card.set)
                }

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    StarWarsDestinyDeckbuilderTheme {
        Greeting()
    }
}
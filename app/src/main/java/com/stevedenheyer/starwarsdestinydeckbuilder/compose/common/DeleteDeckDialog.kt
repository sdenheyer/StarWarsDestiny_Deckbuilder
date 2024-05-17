package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable


@Composable
fun DeleteDeckDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    deckName: String,
) {
    AlertDialog(title = { Text("Are you sure you want to delete $deckName?\nThis cannot be undone.") },
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.secondary,
        textContentColor = MaterialTheme.colorScheme.onSecondary,
        titleContentColor = MaterialTheme.colorScheme.onSecondary,
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text("Yes", color = MaterialTheme.colorScheme.onSecondary)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest }) {
                Text("No", color = MaterialTheme.colorScheme.onSecondary)
            }
        }
    )
}
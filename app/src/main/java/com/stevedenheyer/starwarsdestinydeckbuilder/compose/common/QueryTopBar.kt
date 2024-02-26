package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryTopBar(queryText: TextFieldValue, setQueryText: (TextFieldValue) -> Unit, submitQuery: (String) -> Unit, openDrawer: () -> Unit) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        title = {
            TextField(
                value = queryText,
                onValueChange = { setQueryText(it) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    keyboardType = KeyboardType.Email,
                    autoCorrect = false
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        submitQuery(queryText.text)
                        //(cardVM::findCard)(queryText.text)
                    }
                ),
                modifier = Modifier.clickable {
                    setQueryText(TextFieldValue(""))
                    keyboardController?.show()
                }
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                openDrawer()
                ///scope.launch { drawerState.apply { if (isClosed) open() else close() } }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        }
    )
}
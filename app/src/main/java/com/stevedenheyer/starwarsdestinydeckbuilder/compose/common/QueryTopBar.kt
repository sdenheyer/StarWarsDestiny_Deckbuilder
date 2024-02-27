package com.stevedenheyer.starwarsdestinydeckbuilder.compose.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.stevedenheyer.starwarsdestinydeckbuilder.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueryTopBar(queryText: TextFieldValue,
                setQueryText: (TextFieldValue) -> Unit,
                submitQuery: (String) -> Unit,
                openDrawer: () -> Unit,
                openSortMenu: () -> Unit,) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val sortMenuExpanded = remember { mutableStateOf(false) }

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
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
        },
        actions = {
            IconButton(onClick = { sortMenuExpanded.value = true }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_sort_24),
                    contentDescription = "Sort",
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onPrimaryContainer)
                )
            }
            DropdownMenu(expanded = sortMenuExpanded.value,
                onDismissRequest = { sortMenuExpanded.value = false },
                offset = DpOffset(x = 0.dp, y = 8.dp),
                modifier = Modifier.background(color = MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(8.dp))
                ) {
                val menuItemColors = MenuItemColors(textColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    leadingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    trailingIconColor = MaterialTheme.colorScheme.onSecondary,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer)
                DropdownMenuItem(text = { Text("Sort by Name") }, colors = menuItemColors, onClick = { /*TODO*/ })
                DropdownMenuItem(text = { Text("Sort by Set") }, colors = menuItemColors, onClick = { /*TODO*/ })
                DropdownMenuItem(text = { Text("Sort by Faction") }, colors = menuItemColors, onClick = { /*TODO*/ })
                DropdownMenuItem(text = { Text("Sort by Points/Cost") }, colors = menuItemColors, onClick = { /*TODO*/ })
            }
        }
    )
}
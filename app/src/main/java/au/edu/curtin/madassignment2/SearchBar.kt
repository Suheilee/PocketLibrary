package au.edu.curtin.madassignment2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun SearchBar(
    searchQuery: String = "",
    onSearch: (String) -> Unit = {}
) {
    var query by remember { mutableStateOf(searchQuery) }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Keep query synced
    LaunchedEffect(searchQuery) {
        query = searchQuery
    }

    OutlinedTextField(
        value = query,
        onValueChange = { query = it }, // no auto search on each letter
        placeholder = {
            Text(
                text = "Search books by title or author...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        query = ""
                        onSearch("")
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearch(query)
            }
        )
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    PocketLibraryTheme { SearchBar() }
}
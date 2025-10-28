package au.edu.curtin.madassignment2

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun SearchBar(
    searchQuery: String = "",
    onSearch: (String) -> Unit = {},
    isOnline: Boolean = true,
    myLibraryMode: Boolean = false
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearch,
        placeholder = {
            Text(
                text = if (isOnline) {
                    "Search books by title or author..."
                } else {
                    "Search saved books..."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = if (isOnline) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                IconButton(
                    onClick = {
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
        supportingText = if (myLibraryMode && searchQuery.isEmpty()){
            { Text("Searching My Library", style = MaterialTheme.typography.bodySmall) }
        } else
            if (!isOnline && searchQuery.isEmpty()) {
                { Text("Offline - Searching saved books only", style = MaterialTheme.typography.bodySmall) }
            } else null
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    PocketLibraryTheme { SearchBar() }
}

@Preview(showBackground = true)
@Composable
fun SearchBarOfflinePreview() {
    PocketLibraryTheme { SearchBar(isOnline = false) }
}
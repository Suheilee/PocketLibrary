package au.edu.curtin.madassignment2

import PocketLibraryTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun NavigationScreen() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var selectedBook by remember { mutableStateOf<Book?>(null) }

    when (currentScreen) {
        is Screen.Main -> {
            MainScreen(
                onBookClick = { book ->
                    selectedBook = book
                    currentScreen = Screen.BookDetail
                }
            )
        }
        is Screen.BookDetail -> {
            selectedBook?.let { book ->
                BookDetailScreen(
                    book = book,
                    onBackClick = {
                        currentScreen = Screen.Main
                    },
                    onShareClick = {
                        // Handle share functionality
                    },
                    onFavoriteToggle = { isFavorite ->
                        // Handle favorite toggle
                    }
                )
            }
        }
    }
}

sealed class Screen {
    object Main : Screen()
    object BookDetail : Screen()
}

@Preview(showBackground = true)
@Composable
fun NavigationScreenPreview() {
    PocketLibraryTheme {
        NavigationScreen()
    }
}
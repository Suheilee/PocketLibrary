package au.edu.curtin.madassignment2

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

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
                    onBackClick = { currentScreen = Screen.Main },
                    onShareClick = { /* TODO: Implement share functionality */ },
                    onFavoriteToggle = { bookId ->
                        // Update the selected book's favorite status for UI only
                        selectedBook = selectedBook?.copy(isFavorite = !book.isFavorite)
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
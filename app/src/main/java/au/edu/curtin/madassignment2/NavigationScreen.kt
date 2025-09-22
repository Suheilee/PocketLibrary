package au.edu.curtin.madassignment2

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun NavigationScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Single shared state manager instance
    val stateManager = remember {
        BookStateManager(context, coroutineScope)
    }

    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    when (currentScreen) {
        is Screen.Main -> {
            MainScreen(
                onBookClick = { book ->
                    selectedBook = book
                    currentScreen = Screen.BookDetail
                },
                stateManager = stateManager
            )
        }
        is Screen.BookDetail -> {
            selectedBook?.let { book ->
                BookDetailScreen(
                    book = book,
                    onBackClick = { currentScreen = Screen.Main },
                    onShareClick = { /* TODO share */ },
                    onFavoriteToggle = { bookId ->
                        stateManager.toggleFavorite(bookId)
                        // Reflect the change on the local selected book
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
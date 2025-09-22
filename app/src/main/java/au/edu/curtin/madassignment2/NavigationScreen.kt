package au.edu.curtin.madassignment2

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun NavigationScreen(
    viewModel: BookViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }
    var selectedBook by remember { mutableStateOf<BookEntity?>(null) }

    when (currentScreen) {
        is Screen.Main -> {
            MainScreen(
                onBookClick = { book ->
                    selectedBook = book
                    currentScreen = Screen.BookDetail
                },
                viewModel = viewModel
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
                        // Could implement sharing the book details
                    },
                    onFavoriteToggle = { isbn ->
                        viewModel.toggleFavorite(isbn)
                        // Update the selectedBook to reflect the change
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
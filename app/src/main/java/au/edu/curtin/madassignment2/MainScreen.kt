package au.edu.curtin.madassignment2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun MainScreen(
    onBookClick: (BookEntity) -> Unit = {},
    stateManager: BookStateManager? = null
) {
    // Always use the provided state manager (created in NavigationScreen)
    val bookStateManager = stateManager!!

    // Get state values
    val books by bookStateManager.filteredBooks
    val categories by bookStateManager.categories
    val selectedCategory by bookStateManager.selectedCategory
    val showFavoritesOnly by bookStateManager.showFavoritesOnly
    val searchQuery by bookStateManager.searchQuery
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // App title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pocket Library",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App tagline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Your small pocket library",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            SearchBar(
                searchQuery = searchQuery,
                onSearch = { query -> bookStateManager.updateSearchQuery(query) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter bar
            CategoryFilterBar(
                categories = categories,
                selectedCategory = selectedCategory,
                showFavoritesOnly = showFavoritesOnly,
                onCategorySelected = { category -> bookStateManager.updateSelectedCategory(category) },
                onFavoritesToggle = { bookStateManager.toggleFavoritesFilter() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${books.size} ${if (books.size == 1) "book" else "books"} found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Books list
            if (books.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No books found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Try adjusting your search or filters",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(books) { book ->
                        BookCard(
                            book = book,
                            onBookClick = onBookClick,
                            onFavoriteClick = { bookId -> bookStateManager.toggleFavorite(bookId) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PocketLibraryTheme {
        MainScreenContent(
            books = listOf(
                BookEntity(
                    id = "1",
                    isbn = "9780439139601",
                    title = "Harry Potter and the Philosopher's Stone",
                    author = "J.K. Rowling",
                    year = 1997,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "The first book in the magical Harry Potter series follows young Harry as he discovers he's a wizard and begins his journey at Hogwarts School of Witchcraft and Wizardry.",
                    isFavorite = true
                )
            ),
            categories = listOf("All", "Fantasy"),
            selectedCategory = "All",
            showFavoritesOnly = false,
            searchQuery = "",
            onBookClick = {},
            onSearch = {},
            onCategorySelected = {},
            onFavoritesToggle = {},
            onFavoriteClick = {}
        )
    }
}

@Composable
private fun MainScreenContent(
    books: List<BookEntity>,
    categories: List<String>,
    selectedCategory: String,
    showFavoritesOnly: Boolean,
    searchQuery: String,
    onBookClick: (BookEntity) -> Unit,
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFavoritesToggle: () -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    // (unchanged – used only for preview)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) { /* …exactly like above… */ }
}
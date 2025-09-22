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
    onBookClick: (BookEntity) -> Unit = {}
) {
    // Sample data for UI only
    val sampleBooks = remember {
        listOf(
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
            ),
            BookEntity(
                id = "2",
                isbn = "9780439064873",
                title = "Harry Potter and the Chamber of Secrets",
                author = "J.K. Rowling",
                year = 1998,
                category = "Fantasy",
                coverImageRes = android.R.drawable.ic_menu_gallery,
                description = "Harry's second year at Hogwarts brings new challenges...",
                isFavorite = false
            ),
            BookEntity(
                id = "3",
                isbn = "9780141439518",
                title = "Pride and Prejudice",
                author = "Jane Austen",
                year = 1813,
                category = "Romance",
                coverImageRes = android.R.drawable.ic_menu_gallery,
                description = "A romantic novel of manners written by Jane Austen.",
                isFavorite = false
            )
        )
    }

    val categories = remember { listOf("All", "Fantasy", "Romance", "Mystery") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showFavoritesOnly by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter books based on current selections
    val filteredBooks = remember(sampleBooks, selectedCategory, showFavoritesOnly, searchQuery) {
        sampleBooks.filter { book ->
            val matchesSearch = if (searchQuery.isBlank()) true
            else book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery, ignoreCase = true)

            val matchesCategory = if (selectedCategory == "All") true else book.category == selectedCategory
            val matchesFavorites = if (showFavoritesOnly) book.isFavorite else true

            matchesSearch && matchesCategory && matchesFavorites
        }
    }

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
                onSearch = { query -> searchQuery = query }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category filter bar
            CategoryFilterBar(
                categories = categories,
                selectedCategory = selectedCategory,
                showFavoritesOnly = showFavoritesOnly,
                onCategorySelected = { category -> selectedCategory = category },
                onFavoritesToggle = { showFavoritesOnly = !showFavoritesOnly }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Results count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${filteredBooks.size} ${if (filteredBooks.size == 1) "book" else "books"} found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Books list
            if (filteredBooks.isEmpty()) {
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
                    items(filteredBooks) { book ->
                        BookCard(
                            book = book,
                            onBookClick = onBookClick,
                            onFavoriteClick = { /* Handle favorite toggle in UI only version */ }
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
        MainScreen()
    }
}
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
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun MainScreen(
    onBookClick: (BookEntity) -> Unit = {},
    viewModel: BookViewModel = viewModel()
) {
    val books by viewModel.books.collectAsState(initial = emptyList())
    val categories by viewModel.allCategories.collectAsState(initial = emptyList())
    val selectedCategory by viewModel.selectedCategory.collectAsState(initial = "All")
    val showFavoritesOnly by viewModel.showFavoritesOnly.collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            onSearch = { query ->
                viewModel.updateSearchQuery(query)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter bar
        CategoryFilterBar(
            categories = categories,
            selectedCategory = selectedCategory,
            showFavoritesOnly = showFavoritesOnly,
            onCategorySelected = { category ->
                viewModel.updateSelectedCategory(category)
            },
            onFavoritesToggle = {
                viewModel.toggleFavoritesFilter()
            }
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onBookClick = onBookClick,
                        onFavoriteClick = { isbn ->
                            viewModel.toggleFavorite(isbn)
                        }
                    )
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
                    isbn = "9780064404990",
                    title = "The Lion, the Witch and the Wardrobe",
                    author = "C.S. Lewis",
                    year = 1950,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Four children discover a magical world beyond a wardrobe door, where they must help the great lion Aslan defeat the evil White Witch and restore peace to Narnia.",
                    isFavorite = false
                ),
                BookEntity(
                    isbn = "9780810993136",
                    title = "Diary of a Wimpy Kid",
                    author = "Jeff Kinney",
                    year = 2007,
                    category = "Children's Humor",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Follow Greg Heffley, a middle school student, as he navigates the awkward world of adolescence through his hilarious diary entries and stick-figure drawings.",
                    isFavorite = false
                )
            ),
            categories = listOf("All", "Fantasy", "Children's Humor", "Science Fiction"),
            selectedCategory = "All",
            showFavoritesOnly = false,
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
    onBookClick: (BookEntity) -> Unit,
    onSearch: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onFavoritesToggle: () -> Unit,
    onFavoriteClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        SearchBar(onSearch = onSearch)

        Spacer(modifier = Modifier.height(16.dp))

        // Category filter bar
        CategoryFilterBar(
            categories = categories,
            selectedCategory = selectedCategory,
            showFavoritesOnly = showFavoritesOnly,
            onCategorySelected = onCategorySelected,
            onFavoritesToggle = onFavoritesToggle
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onBookClick = onBookClick,
                        onFavoriteClick = onFavoriteClick
                    )
                }
            }
        }
    }
}
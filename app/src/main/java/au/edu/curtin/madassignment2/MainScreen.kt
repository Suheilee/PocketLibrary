package au.edu.curtin.madassignment2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun MainScreen(vm: BooksViewModel = viewModel()) {
    val state by vm.state.collectAsState()

    // Use predefined categories or dynamic categories based on available books
    val categories = if (state.allBooks.isEmpty()) {
        listOf("All", "Fantasy", "Romance", "Classic", "Mystery", "General")
    } else {
        state.availableCategories.ifEmpty {
            listOf("All", "Fantasy", "Romance", "Classic", "Mystery", "General")
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Pocket Library",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            SearchBar(
                searchQuery = state.searchQuery,
                onSearch = vm::onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            CategoryFilterBar(
                categories = categories,
                selectedCategory = state.selectedCategory,
                showFavoritesOnly = state.showFavoritesOnly,
                onCategorySelected = vm::onCategorySelected,
                onFavoritesToggle = vm::toggleFavoritesOnly
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.filteredBooks.isEmpty() && state.searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (state.showFavoritesOnly) {
                                    if (state.selectedCategory != "All") {
                                        "No favorite ${state.selectedCategory} books yet"
                                    } else {
                                        "No favorite books yet"
                                    }
                                } else {
                                    if (state.selectedCategory != "All") {
                                        "No ${state.selectedCategory} books in your favorites"
                                    } else {
                                        "Your favorite books will appear here"
                                    }
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Search for books to add to your favorites",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                state.filteredBooks.isEmpty() && state.searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.selectedCategory != "All") {
                                "No ${state.selectedCategory} books found for \"${state.searchQuery}\""
                            } else {
                                "No books found for \"${state.searchQuery}\""
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.filteredBooks) { book ->
                            BookCard(
                                book = book,
                                onFavoriteClick = vm::toggleFavorite
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PocketLibraryTheme { MainScreen() }
}
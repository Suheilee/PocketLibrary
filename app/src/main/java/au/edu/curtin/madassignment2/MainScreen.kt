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
        ) {
            // Search Bar
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                SearchBar(
                    searchQuery = state.searchQuery,
                    onSearch = vm::onSearchQueryChange
                )
            }

            // Category Filter
            CategoryFilterBar(
                categories = categories,
                selectedCategory = state.selectedCategory,
                onCategorySelected = vm::onCategorySelected
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Content Area
            when {
                state.filteredBooks.isEmpty() && state.searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (state.selectedCategory != "All") {
                                    "No ${state.selectedCategory} books saved"
                                } else {
                                    "No books saved yet"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Search for books to add them to your library",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                state.filteredBooks.isEmpty() && state.searchQuery.isNotEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = if (state.selectedCategory != "All") {
                                    "No ${state.selectedCategory} books found"
                                } else {
                                    "No books found"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "for \"${state.searchQuery}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
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
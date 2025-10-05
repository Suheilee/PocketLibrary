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
    val categories = listOf("All", "Fantasy", "Romance", "Classic", "Mystery")

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
                onSearch = vm::onSearch
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
                state.searchQuery.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Search for books to get started",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                state.filteredBooks.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No books found",
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
                                onBookClick = { /* TODO: details screen */ },
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
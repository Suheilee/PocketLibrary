package au.edu.curtin.madassignment2

import android.content.Intent
import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun MainScreen(vm: BooksViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Adjust grid columns based on orientation
    val gridColumns = if (isLandscape) 3 else 2

    // Use predefined categories or dynamic categories based on available books
    val categories = if (state.allBooks.isEmpty()) {
        listOf("All", "Fantasy", "Romance", "Classic", "Mystery", "General")
    } else {
        state.availableCategories.ifEmpty {
            listOf("All", "Fantasy", "Romance", "Classic", "Mystery", "General")
        }
    }

    // Share book
    val shareBook: (BookEntity) -> Unit = { book ->
        val shareText = buildString {
            append("Title: ${book.title}\n")
            append("Author: ${book.author}\n")
            append("Year: ${book.year}\n")
            append("Category: ${book.category}\n")
            if (book.coverImageUrl != null) {
                append("\nCover: ${book.coverImageUrl}")
            }
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share book via")
        context.startActivity(shareIntent)
    }

    Scaffold { innerPadding ->
        if (isLandscape) {
            // Landscape Layout - Two column layout with filters on left
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Left side - Search and Category filters
                Column(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                        .padding(16.dp)
                ) {
                    SearchBar(
                        searchQuery = state.searchQuery,
                        onSearch = vm::onSearchQueryChange
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Vertical category list for landscape
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            FilterChip(
                                onClick = { vm.onCategorySelected(category) },
                                label = {
                                    Text(
                                        text = category,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                selected = state.selectedCategory == category,
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurface,
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Right side - Book grid
                Box(modifier = Modifier.weight(1f)) {
                    BookGridContent(
                        state = state,
                        gridColumns = gridColumns,
                        onToggleFavorite = vm::toggleFavorite,
                        onShareBook = shareBook
                    )
                }
            }
        } else {
            // Portrait Layout - Original vertical layout
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

                // Book grid
                BookGridContent(
                    state = state,
                    gridColumns = gridColumns,
                    onToggleFavorite = vm::toggleFavorite,
                    onShareBook = shareBook
                )
            }
        }
    }
}

@Composable
fun BookGridContent(
    state: BooksUiState,
    gridColumns: Int,
    onToggleFavorite: (String) -> Unit,
    onShareBook: (BookEntity) -> Unit
) {
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
                columns = GridCells.Fixed(gridColumns),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.filteredBooks) { book ->
                    BookCard(
                        book = book,
                        onFavoriteClick = onToggleFavorite,
                        onShareClick = onShareBook
                    )
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

@Preview(showBackground = true, widthDp = 720, heightDp = 360)
@Composable
fun MainScreenLandscapePreview() {
    PocketLibraryTheme { MainScreen() }
}
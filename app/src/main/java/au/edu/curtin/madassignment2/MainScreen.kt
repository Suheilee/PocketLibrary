package au.edu.curtin.madassignment2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun MainScreen(vm: BooksViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Camera permission state
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Dialog state for remove cover confirmation
    var bookToRemoveCover by remember { mutableStateOf<BookEntity?>(null) }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Camera launcher - handles photo capture
    var selectedBookIdForPhoto by remember { mutableStateOf<String?>(null) }
    val launchCamera = rememberCameraLauncher { photoPath ->
        selectedBookIdForPhoto?.let { bookId ->
            vm.updateBookCoverPhoto(bookId, photoPath)
        }
        selectedBookIdForPhoto = null
    }

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

    // Handle camera click - request permission if needed, then launch camera
    val onCameraClick: (String) -> Unit = { bookId ->
        selectedBookIdForPhoto = bookId
        if (hasCameraPermission) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Handle remove cover click - show confirmation dialog
    val onRemoveCoverClick: (String) -> Unit = { bookId ->
        val book = state.filteredBooks.find { it.id == bookId }
        bookToRemoveCover = book
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
                        onShareBook = shareBook,
                        onCameraClick = onCameraClick,
                        onRemoveCoverClick = onRemoveCoverClick
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
                    onShareBook = shareBook,
                    onCameraClick = onCameraClick,
                    onRemoveCoverClick = onRemoveCoverClick
                )
            }
        }
    }

    // Remove Cover Confirmation Dialog
    bookToRemoveCover?.let { book ->
        AlertDialog(
            onDismissRequest = { bookToRemoveCover = null },
            icon = {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_delete),
                    contentDescription = "Remove Cover",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Remove Cover Photo",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to remove the custom cover photo for:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The original cover will be restored.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.removeBookCoverPhoto(book.id)
                        bookToRemoveCover = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { bookToRemoveCover = null }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BookGridContent(
    state: BooksUiState,
    gridColumns: Int,
    onToggleFavorite: (String) -> Unit,
    onShareBook: (BookEntity) -> Unit,
    onCameraClick: (String) -> Unit,
    onRemoveCoverClick: (String) -> Unit
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
                items(
                    items = state.filteredBooks,
                    key = { book -> book.id }
                ) { book ->
                    BookCard(
                        book = book,
                        onFavoriteClick = onToggleFavorite,
                        onShareClick = onShareBook,
                        onCameraClick = onCameraClick,
                        onRemoveCoverClick = onRemoveCoverClick
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
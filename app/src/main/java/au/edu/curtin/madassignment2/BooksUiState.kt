package au.edu.curtin.madassignment2

data class BooksUiState(
    val allBooks: List<BookEntity> = emptyList(),
    val selectedCategory: String = "All",
    val showFavoritesOnly: Boolean = false,
    val searchQuery: String = "",
    val favorites: Set<String> = emptySet(),
    val isOnline: Boolean = true,
    val isLibraryMode: Boolean = false
) {
    val filteredBooks: List<BookEntity>
        get() = allBooks
            .map { b -> if (favorites.contains(b.id)) b.copy(isFavorite = true) else b }
            .filter { book ->
                // Filter by category that works for both search results and database favorites
                val matchesCategory =
                    selectedCategory == "All" || book.category == selectedCategory

                // Filter by favorites if toggle is on
                val matchesFav =
                    if (showFavoritesOnly) book.isFavorite else true
                // Local search filter (for offline mode)
                val matchesSearch = if (searchQuery.isNotBlank() && !isOnline) {
                    book.title.contains(searchQuery, ignoreCase = true) ||
                            book.author.contains(searchQuery, ignoreCase = true)
                } else {
                    true
                }
                matchesCategory && matchesFav
            }

    // Get available categories from current books
    val availableCategories: List<String>
        get() {
            val categories = allBooks
                .map { it.category }
                .distinct()
                .sorted()
            return listOf("All") + categories
        }
}
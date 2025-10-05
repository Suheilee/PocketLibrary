package au.edu.curtin.madassignment2

data class BooksUiState(
    val allBooks: List<BookEntity> = emptyList(),
    val selectedCategory: String = "All",
    val showFavoritesOnly: Boolean = false,
    val searchQuery: String = "",
    val favorites: Set<String> = emptySet()
) {
    val filteredBooks: List<BookEntity>
        get() = allBooks
            .map { b -> if (favorites.contains(b.id)) b.copy(isFavorite = true) else b }
            .filter { book ->
                val matchesCategory =
                    selectedCategory == "All" || book.category == selectedCategory

                val matchesFav =
                    if (showFavoritesOnly) book.isFavorite else true

                matchesCategory && matchesFav
            }
}
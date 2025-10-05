package au.edu.curtin.madassignment2

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BooksViewModel : ViewModel() {
    private val sampleBooks = listOf(
        BookEntity("1","Harry Potter and the Philosopher's Stone","J.K. Rowling",1997,"Fantasy",coverImageRes = android.R.drawable.ic_menu_gallery, isFavorite = true),
        BookEntity("2","Harry Potter and the Chamber of Secrets","J.K. Rowling",1998,"Fantasy",coverImageRes = android.R.drawable.ic_menu_gallery),
        BookEntity("3","Pride and Prejudice","Jane Austen",1813,"Romance",coverImageRes = android.R.drawable.ic_menu_gallery),
        BookEntity("4","The Great Gatsby","F. Scott Fitzgerald",1925,"Classic",coverImageRes = android.R.drawable.ic_menu_gallery, isFavorite = true),
        BookEntity("5","To Kill a Mockingbird","Harper Lee",1960,"Classic",coverImageRes = android.R.drawable.ic_menu_gallery)
    )

    private val _state = MutableStateFlow(
        BooksUiState(
            allBooks = sampleBooks,
            favorites = sampleBooks.filter { it.isFavorite }.map { it.id }.toSet()
        )
    )
    val state: StateFlow<BooksUiState> = _state.asStateFlow()

    fun onSearch(query: String) = _state.update { it.copy(searchQuery = query) }
    fun onCategorySelected(category: String) = _state.update { it.copy(selectedCategory = category) }
    fun toggleFavoritesOnly() = _state.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }
    fun toggleFavorite(id: String) = _state.update {
        val next = it.favorites.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
        it.copy(favorites = next)
    }
}
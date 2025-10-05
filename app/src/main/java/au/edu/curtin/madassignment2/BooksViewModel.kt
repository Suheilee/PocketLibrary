package au.edu.curtin.madassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BooksViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = FavoriteBookRepository(database.favoriteBookDao())

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(OpenLibraryApi::class.java)

    private val _state = MutableStateFlow(BooksUiState())

    // Combine API books with database favorites
    val state: StateFlow<BooksUiState> = combine(
        _state,
        repository.allFavoriteIds,
        repository.allFavorites
    ) { state, favoriteIds, favoriteBooks ->
        // Convert database favorites to BookEntity
        val favoriteBooksAsEntities = favoriteBooks.map { favBook ->
            BookEntity(
                id = favBook.id,
                title = favBook.title,
                author = favBook.author,
                year = favBook.year,
                category = favBook.category,
                coverImageUrl = favBook.coverImageUrl,
                isFavorite = true
            )
        }

        // Determine which books to show based on search state
        val displayBooks = if (state.searchQuery.isBlank()) {
            // No search - show favorites from database
            favoriteBooksAsEntities
        } else {
            // Active search - show API results with updated favorite status
            state.allBooks
        }

        state.copy(
            favorites = favoriteIds.toSet(),
            allBooks = displayBooks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BooksUiState()
    )

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .filter { it.isNotBlank() }
                .collect { query ->
                    fetchBooksFromApi(query)
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            _state.update { it.copy(allBooks = emptyList()) }
        }
    }

    private fun categorizeBook(title: String, author: String): String {
        val titleLower = title.lowercase()
        val authorLower = author.lowercase()

        return when {
            titleLower.contains("harry potter") ||
                    titleLower.contains("lord of the rings") ||
                    titleLower.contains("hobbit") ||
                    titleLower.contains("narnia") ||
                    authorLower.contains("tolkien") ||
                    authorLower.contains("rowling") -> "Fantasy"

            titleLower.contains("pride and prejudice") ||
                    titleLower.contains("outlander") ||
                    authorLower.contains("austen") ||
                    authorLower.contains("romance") -> "Romance"

            titleLower.contains("sherlock") ||
                    titleLower.contains("murder") ||
                    titleLower.contains("detective") ||
                    titleLower.contains("mystery") ||
                    authorLower.contains("christie") ||
                    authorLower.contains("doyle") -> "Mystery"

            titleLower.contains("1984") ||
                    titleLower.contains("gatsby") ||
                    titleLower.contains("mockingbird") ||
                    titleLower.contains("catcher") ||
                    authorLower.contains("orwell") ||
                    authorLower.contains("fitzgerald") ||
                    authorLower.contains("steinbeck") -> "Classic"

            else -> "General"
        }
    }

    private fun fetchBooksFromApi(query: String) {
        viewModelScope.launch {
            try {
                println("🔍 Searching for: $query")
                val response = api.searchBooks(query)
                println("📡 API Response: ${response.docs.size} docs")

                val books = response.docs.mapNotNull { book ->
                    if (book.key != null && book.title != null) {
                        val author = book.author_name?.joinToString(", ") ?: "Unknown Author"
                        BookEntity(
                            id = book.key,
                            title = book.title,
                            author = author,
                            year = book.first_publish_year ?: 0,
                            coverImageUrl = book.cover_i?.let {
                                "https://covers.openlibrary.org/b/id/$it-M.jpg"
                            },
                            category = categorizeBook(book.title, author)
                        )
                    } else null
                }
                _state.update {
                    it.copy(allBooks = books)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(allBooks = emptyList()) }
            }
        }
    }

    fun onCategorySelected(category: String) =
        _state.update { it.copy(selectedCategory = category) }

    fun toggleFavoritesOnly() =
        _state.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            val currentState = state.value
            val isFavorite = currentState.favorites.contains(bookId)

            if (isFavorite) {
                // Remove from database
                repository.deleteFavoriteById(bookId)

                // If we're viewing database books (no search), update the local list
                if (currentState.searchQuery.isBlank()) {
                    _state.update {
                        it.copy(allBooks = it.allBooks.filter { book -> book.id != bookId })
                    }
                }
            } else {
                // Find the book to add
                val currentBook = currentState.allBooks.find { it.id == bookId }

                if (currentBook != null) {
                    // Add to database
                    val favoriteBook = FavoriteBookEntity(
                        id = currentBook.id,
                        title = currentBook.title,
                        author = currentBook.author,
                        year = currentBook.year,
                        category = currentBook.category,
                        coverImageUrl = currentBook.coverImageUrl
                    )
                    repository.insertFavorite(favoriteBook)
                }
            }
        }
    }
}
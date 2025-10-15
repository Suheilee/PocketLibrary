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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BooksViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val firestoreService = FirestoreService()
    private val repository = FavoriteBookRepository(database.favoriteBookDao(), firestoreService)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(OpenLibraryApi::class.java)

    private val _state = MutableStateFlow(BooksUiState())
    private val _isLoading = MutableStateFlow(false)
    private val _syncStatus = MutableStateFlow("")

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

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
                localCoverPhotoPath = favBook.localCoverPhotoPath,
                isFavorite = true
            )
        }

        // Determine which books to show based on search state
        val displayBooks = if (state.searchQuery.isBlank()) {
            // No search - show favorites from database
            favoriteBooksAsEntities
        } else {
            // Active search shows API results with updated favorite status
            state.allBooks.map { book ->
                if (favoriteIds.contains(book.id)) {
                    book.copy(isFavorite = true)
                } else {
                    book
                }
            }
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

        // Sync with cloud on startup if user is logged in
        viewModelScope.launch {
            if (Firebase.auth.currentUser != null) {
                syncWithCloud()
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
                    titleLower.contains("fantasy") ||
                    titleLower.contains("dragon") ||
                    titleLower.contains("wizard") ||
                    titleLower.contains("magic") ||
                    authorLower.contains("tolkien") ||
                    authorLower.contains("rowling") ||
                    authorLower.contains("sanderson") -> "Fantasy"

            titleLower.contains("pride and prejudice") ||
                    titleLower.contains("outlander") ||
                    titleLower.contains("romance") ||
                    titleLower.contains("love") ||
                    authorLower.contains("austen") ||
                    authorLower.contains("sparks") -> "Romance"

            titleLower.contains("sherlock") ||
                    titleLower.contains("murder") ||
                    titleLower.contains("detective") ||
                    titleLower.contains("mystery") ||
                    titleLower.contains("crime") ||
                    authorLower.contains("christie") ||
                    authorLower.contains("doyle") ||
                    authorLower.contains("conan doyle") -> "Mystery"

            titleLower.contains("1984") ||
                    titleLower.contains("gatsby") ||
                    titleLower.contains("mockingbird") ||
                    titleLower.contains("catcher") ||
                    titleLower.contains("moby dick") ||
                    titleLower.contains("jane eyre") ||
                    authorLower.contains("orwell") ||
                    authorLower.contains("fitzgerald") ||
                    authorLower.contains("steinbeck") ||
                    authorLower.contains("dickens") ||
                    authorLower.contains("hemingway") -> "Classic"

            else -> "General"
        }
    }

    private fun fetchBooksFromApi(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentCategory = _state.value.selectedCategory

                val searchQuery = if (currentCategory != "All") {
                    "$query subject:${currentCategory.lowercase()}"
                } else {
                    query
                }

                val response = api.searchBooks(searchQuery)

                val books = response.docs.mapNotNull { book ->
                    if (book.key != null && book.title != null) {
                        val author = book.author_name?.joinToString(", ") ?: "Unknown Author"
                        val category = categorizeBook(book.title, author)

                        BookEntity(
                            id = book.key,
                            title = book.title,
                            author = author,
                            year = book.first_publish_year ?: 0,
                            coverImageUrl = book.cover_i?.let {
                                "https://covers.openlibrary.org/b/id/$it-M.jpg"
                            },
                            category = category
                        )
                    } else null
                }

                _state.update {
                    it.copy(allBooks = books)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { it.copy(allBooks = emptyList()) }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(category: String) {
        _state.update { it.copy(selectedCategory = category) }

        val currentQuery = _state.value.searchQuery
        if (currentQuery.isNotBlank()) {
            fetchBooksFromApi(currentQuery)
        }
    }

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
                    // Add to database with the properly categorized category
                    val favoriteBook = FavoriteBookEntity(
                        id = currentBook.id,
                        title = currentBook.title,
                        author = currentBook.author,
                        year = currentBook.year,
                        category = currentBook.category,
                        coverImageUrl = currentBook.coverImageUrl,
                        localCoverPhotoPath = null
                    )
                    repository.insertFavorite(favoriteBook)

                    // Update the UI immediately for better UX
                    _state.update { currentState ->
                        currentState.copy(
                            allBooks = currentState.allBooks.map { book ->
                                if (book.id == bookId) book.copy(isFavorite = true) else book
                            }
                        )
                    }
                }
            }
        }
    }

    fun updateBookCoverPhoto(bookId: String, photoPath: String) {
        viewModelScope.launch {
            repository.updateCoverPhoto(bookId, photoPath)

            // Update the UI immediately
            _state.update { currentState ->
                currentState.copy(
                    allBooks = currentState.allBooks.map { book ->
                        if (book.id == bookId) book.copy(localCoverPhotoPath = photoPath) else book
                    }
                )
            }
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            _isLoading.value = true
            _syncStatus.value = "Syncing with cloud..."
            try {
                repository.syncWithCloud()
                _syncStatus.value = "Sync completed successfully"
            } catch (e: Exception) {
                e.printStackTrace()
                _syncStatus.value = "Sync failed: ${e.message}"
            } finally {
                _isLoading.value = false
                // Clear sync status after 3 seconds
                viewModelScope.launch {
                    kotlinx.coroutines.delay(3000)
                    _syncStatus.value = ""
                }
            }
        }
    }

    fun checkUserStatus(): Boolean {
        return Firebase.auth.currentUser != null
    }

    fun getCurrentUserId(): String? {
        return Firebase.auth.currentUser?.uid
    }

    // Manual entry for offline books
    fun addManualBook(title: String, author: String, year: Int, category: String = "General") {
        viewModelScope.launch {
            val bookId = "manual_${System.currentTimeMillis()}"
            val favoriteBook = FavoriteBookEntity(
                id = bookId,
                title = title,
                author = author,
                year = year,
                category = category,
                coverImageUrl = null,
                localCoverPhotoPath = null
            )

            repository.insertFavorite(favoriteBook)

            // Update UI to show the new book
            if (_state.value.searchQuery.isBlank()) {
                _state.update { currentState ->
                    currentState.copy(
                        allBooks = currentState.allBooks + BookEntity(
                            id = bookId,
                            title = title,
                            author = author,
                            year = year,
                            category = category,
                            isFavorite = true
                        )
                    )
                }
            }
        }
    }

    // Clear search results
    fun clearSearch() {
        _searchQuery.value = ""
        _state.update { it.copy(searchQuery = "", allBooks = emptyList()) }
    }
}
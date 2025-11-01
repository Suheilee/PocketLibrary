package au.edu.curtin.madassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
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
    private val networkMonitor = NetworkMonitor(application)

    // JSON & API setup
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
    private val api = retrofit.create(OpenLibraryApi::class.java)

    // State and helper flows
    private val _state = MutableStateFlow(BooksUiState())
    private val _isLoading = MutableStateFlow(false)
    private val _syncStatus = MutableStateFlow("")
    private val _searchQuery = MutableStateFlow("")
    private val _isLibraryMode = MutableStateFlow(false)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val syncStatus: StateFlow<String> = _syncStatus.asStateFlow()

    // Combined view state
    val state: StateFlow<BooksUiState> = combine(
        _state,
        repository.allFavoriteIds,
        repository.allFavorites,
        networkMonitor.isOnline,
        _isLibraryMode
    ) { state, favoriteIds, favoriteBooks, isOnline, isLibraryMode ->

        val favoriteBooksAsEntities = favoriteBooks.map { fav ->
            BookEntity(
                id = fav.id,
                title = fav.title,
                author = fav.author,
                year = fav.year,
                category = fav.category,
                coverImageUrl = fav.coverImageUrl,
                localCoverPhotoPath = fav.localCoverPhotoPath,
                isFavorite = true
            )
        }

        val displayBooks =
            if (isLibraryMode) {
                // Always show local favourites in library mode
                val query = state.searchQuery
                if (query.isBlank()) {
                    favoriteBooksAsEntities
                } else {
                    favoriteBooksAsEntities.filter { book ->
                        book.title.contains(query, ignoreCase = true) ||
                                book.author.contains(query, ignoreCase = true)
                    }
                }
            } else if (state.searchQuery.isBlank() || !isOnline) {
                // No search → show favourites
                favoriteBooksAsEntities
            } else {
                // Online search → display fetched results with favourite status updated
                state.allBooks.map { book ->
                    book.copy(isFavorite = favoriteIds.contains(book.id))
                }
            }

        state.copy(
            favorites = favoriteIds.toSet(),
            allBooks = displayBooks,
            isOnline = isOnline,
            isLibraryMode = isLibraryMode
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BooksUiState())

    init {
        // Observe search query changes (for online search only)
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (!_isLibraryMode.value && state.value.isOnline && query.isNotBlank()) {
                        fetchBooksFromApi(query)
                    }
                }
        }

        // Sync cloud favourites on startup if logged in
        viewModelScope.launch {
            if (Firebase.auth.currentUser != null) syncWithCloud()
        }
    }

    // Called from UI when typing in search bar
    fun onSearchQueryChange(query: String, isLibraryMode: Boolean) {
        _isLibraryMode.value = isLibraryMode
        _searchQuery.value = query
        _state.update { it.copy(searchQuery = query) }

        // For Library Mode, filter local favourites manually
        if (isLibraryMode) {
            viewModelScope.launch {
                val favBooks = repository.allFavorites.first()
                val filtered = if (query.isBlank()) {
                    favBooks
                } else {
                    favBooks.filter { book ->
                        book.title.contains(query, ignoreCase = true) ||
                                book.author.contains(query, ignoreCase = true)
                    }
                }.map {
                    BookEntity(
                        id = it.id,
                        title = it.title,
                        author = it.author,
                        year = it.year,
                        category = it.category,
                        coverImageUrl = it.coverImageUrl,
                        localCoverPhotoPath = it.localCoverPhotoPath,
                        isFavorite = true
                    )
                }

                _state.update { current -> current.copy(allBooks = filtered) }
            }
        }
    }

    private fun categorizeBook(title: String, author: String): String {
        val t = title.lowercase()
        val a = author.lowercase()

        return when {
            listOf("harry potter", "lord of the rings", "hobbit", "narnia", "fantasy", "dragon", "wizard", "magic").any { it in t } ||
                    listOf("tolkien", "rowling", "sanderson").any { it in a } -> "Fantasy"
            listOf("pride and prejudice", "outlander", "romance", "love").any { it in t } ||
                    listOf("austen", "sparks").any { it in a } -> "Romance"
            listOf("sherlock", "murder", "detective", "mystery", "crime").any { it in t } ||
                    listOf("christie", "doyle", "conan doyle").any { it in a } -> "Mystery"
            listOf("1984", "gatsby", "mockingbird", "catcher", "moby dick", "jane eyre").any { it in t } ||
                    listOf("orwell", "fitzgerald", "steinbeck", "dickens", "hemingway").any { it in a } -> "Classic"
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
                } else query

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
                _state.update { it.copy(allBooks = books) }
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
        if (_state.value.searchQuery.isNotBlank() && !_isLibraryMode.value) {
            fetchBooksFromApi(_state.value.searchQuery)
        }
    }

    fun toggleFavorite(bookId: String) {
        viewModelScope.launch {
            val current = state.value
            val isFavorite = current.favorites.contains(bookId)

            if (isFavorite) {
                repository.deleteFavoriteById(bookId)
                if (current.searchQuery.isBlank() || _isLibraryMode.value) {
                    _state.update { it.copy(allBooks = it.allBooks.filter { b -> b.id != bookId }) }
                }
            } else {
                val currentBook = current.allBooks.find { it.id == bookId } ?: return@launch
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
                _state.update {
                    it.copy(allBooks = it.allBooks.map { b ->
                        if (b.id == bookId) b.copy(isFavorite = true) else b
                    })
                }
            }
        }
    }

    fun updateBookCoverPhoto(bookId: String, photoPath: String) {
        viewModelScope.launch {
            try {
                repository.updateCoverPhoto(bookId, photoPath)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeBookCoverPhoto(bookId: String) {
        viewModelScope.launch {
            try {
                repository.removeCoverPhoto(bookId)
            } catch (e: Exception) {
                e.printStackTrace()
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
                delay(3000)
                _syncStatus.value = ""
            }
        }
    }

    fun checkUserStatus(): Boolean = Firebase.auth.currentUser != null
    fun getCurrentUserId(): String? = Firebase.auth.currentUser?.uid

    fun addManualBook(title: String, author: String, year: Int, category: String = "General") {
        viewModelScope.launch {
            val id = "manual_${System.currentTimeMillis()}"
            val book = FavoriteBookEntity(
                id = id,
                title = title,
                author = author,
                year = year,
                category = category,
                coverImageUrl = null,
                localCoverPhotoPath = null
            )
            repository.insertFavorite(book)
            if (_state.value.searchQuery.isBlank() || _isLibraryMode.value) {
                _state.update {
                    it.copy(allBooks = it.allBooks + BookEntity(
                        id = id,
                        title = title,
                        author = author,
                        year = year,
                        category = category,
                        isFavorite = true
                    ))
                }
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _state.update { it.copy(searchQuery = "", allBooks = emptyList()) }
    }

    fun updateLibraryMode(isLibraryMode: Boolean) {
        _state.update { it.copy(isLibraryMode = isLibraryMode) }
    }

}

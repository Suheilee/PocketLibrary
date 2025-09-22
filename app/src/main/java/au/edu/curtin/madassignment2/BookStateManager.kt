package au.edu.curtin.madassignment2

import android.content.Context
import androidx.compose.runtime.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BookStateManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    private val repository: BookRepository

    private val _books = mutableStateOf<List<BookEntity>>(emptyList())
    val books: State<List<BookEntity>> = _books

    private val _categories = mutableStateOf<List<String>>(listOf("All"))
    val categories: State<List<String>> = _categories

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    private val _selectedCategory = mutableStateOf("All")
    val selectedCategory: State<String> = _selectedCategory

    private val _showFavoritesOnly = mutableStateOf(false)
    val showFavoritesOnly: State<Boolean> = _showFavoritesOnly

    private val _filteredBooks = mutableStateOf<List<BookEntity>>(emptyList())
    val filteredBooks: State<List<BookEntity>> = _filteredBooks

    init {
        val bookDao = PocketLibraryDatabase.getDatabase(context).bookDao()
        repository = BookRepository(bookDao)

        coroutineScope.launch {
            initializeSampleData()
            loadBooksAndCategories()
        }
    }

    private suspend fun loadBooksAndCategories() {
        coroutineScope.launch {
            repository.getAllBooks().collect { bookList ->
                _books.value = bookList
                updateFilteredBooks()
            }
        }

        coroutineScope.launch {
            repository.getAllCategories().collect { categoryList ->
                _categories.value = listOf("All") + categoryList
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        updateFilteredBooks()
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
        updateFilteredBooks()
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
        updateFilteredBooks()
    }

    fun toggleFavorite(bookId: String) {
        coroutineScope.launch {
            val book = repository.getBookById(bookId)
            book?.let {
                val updatedBook = it.copy(isFavorite = !it.isFavorite)
                repository.updateBook(updatedBook)
            }
        }
    }

    fun addBook(book: BookEntity) {
        coroutineScope.launch { repository.insertBook(book) }
    }

    fun deleteBook(book: BookEntity) {
        coroutineScope.launch { repository.deleteBook(book) }
    }

    private fun updateFilteredBooks() {
        val query = _searchQuery.value
        val category = _selectedCategory.value
        val favoritesOnly = _showFavoritesOnly.value
        val allBooks = _books.value

        val filtered = allBooks.filter { book ->
            val matchesSearch = if (query.isBlank()) true
            else book.title.contains(query, ignoreCase = true) ||
                    book.author.contains(query, ignoreCase = true)

            val matchesCategory = if (category == "All") true else book.category == category
            val matchesFavorites = if (favoritesOnly) book.isFavorite else true

            matchesSearch && matchesCategory && matchesFavorites
        }

        _filteredBooks.value = filtered
    }

    // Prevent duplicate inserts; do DB work on IO
    private suspend fun initializeSampleData() = withContext(Dispatchers.IO) {
        val existing = repository.getAllBooks().first()
        if (existing.isEmpty()) {
            val sampleBooks = listOf(
                BookEntity(
                    id = "book_1",
                    isbn = "9780439139601",
                    title = "Harry Potter and the Philosopher's Stone",
                    author = "J.K. Rowling",
                    year = 1997,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "The first book in the magical Harry Potter series follows young Harry..."
                ),
                BookEntity(
                    id = "book_2",
                    isbn = "9780439064873",
                    title = "Harry Potter and the Chamber of Secrets",
                    author = "J.K. Rowling",
                    year = 1998,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Harry's second year at Hogwarts brings new challenges..."
                )
            )
            repository.insertBooks(sampleBooks)
        }
    }
}
package au.edu.curtin.madassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _showFavoritesOnly = MutableStateFlow(false)
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    val allCategories: Flow<List<String>>
    val books: Flow<List<BookEntity>>

    init {
        val bookDao = PocketLibraryDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)

        // Initialize with sample data
        viewModelScope.launch {
            initializeSampleData()
        }

        allCategories = repository.getAllCategories().map { categories ->
            listOf("All") + categories
        }

        books = combine(
            searchQuery,
            selectedCategory,
            showFavoritesOnly
        ) { query, category, favoritesOnly ->
            Triple(query, category, favoritesOnly)
        }.flatMapLatest { (query, category, favoritesOnly) ->
            when {
                favoritesOnly -> {
                    if (query.isNotBlank()) {
                        repository.searchBooks(query).map { books ->
                            books.filter { it.isFavorite && (category == "All" || it.category == category) }
                        }
                    } else {
                        repository.getFavoriteBooks().map { books ->
                            if (category == "All") books else books.filter { it.category == category }
                        }
                    }
                }
                query.isNotBlank() -> {
                    repository.searchBooks(query).map { books ->
                        if (category == "All") books else books.filter { it.category == category }
                    }
                }
                category != "All" -> {
                    repository.getBooksByCategory(category)
                }
                else -> {
                    repository.getAllBooks()
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun toggleFavoritesFilter() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    fun toggleFavorite(isbn: String) {
        viewModelScope.launch {
            val book = repository.getBookByIsbn(isbn)
            book?.let {
                repository.updateFavoriteStatus(isbn, !it.isFavorite)
            }
        }
    }

    fun addBook(book: BookEntity) {
        viewModelScope.launch {
            repository.insertBook(book)
        }
    }

    fun deleteBook(book: BookEntity) {
        viewModelScope.launch {
            repository.deleteBook(book)
        }
    }

    private suspend fun initializeSampleData() {
        // Check if database is empty
        val existingBooks = repository.getAllBooks().first()
        if (existingBooks.isEmpty()) {
            val sampleBooks = listOf(
                BookEntity(
                    isbn = "9780439139601",
                    title = "Harry Potter and the Philosopher's Stone",
                    author = "J.K. Rowling",
                    year = 1997,
                    category = "Fantasy",
                    coverImageRes = R.drawable.ic_launcher_foreground,
                    description = "The first book in the magical Harry Potter series follows young Harry as he discovers he's a wizard and begins his journey at Hogwarts School of Witchcraft and Wizardry. A tale of friendship, bravery, and the battle between good and evil."
                ),
                BookEntity(
                    isbn = "9780439064873",
                    title = "Harry Potter and the Chamber of Secrets",
                    author = "J.K. Rowling",
                    year = 1998,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Harry's second year at Hogwarts brings new challenges as mysterious attacks plague the school. With the help of his friends Ron and Hermione, Harry must uncover the secret of the Chamber of Secrets."
                ),
                BookEntity(
                    isbn = "9780439136365",
                    title = "Harry Potter and the Prisoner of Azkaban",
                    author = "J.K. Rowling",
                    year = 1999,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "In his third year, Harry learns about his parents' past and encounters the dangerous escaped prisoner Sirius Black. Time travel and patronus magic feature prominently in this darker installment."
                ),
                BookEntity(
                    isbn = "9780064404990",
                    title = "The Lion, the Witch and the Wardrobe",
                    author = "C.S. Lewis",
                    year = 1950,
                    category = "Fantasy",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Four children discover a magical world beyond a wardrobe door, where they must help the great lion Aslan defeat the evil White Witch and restore peace to Narnia."
                ),
                BookEntity(
                    isbn = "9780810993136",
                    title = "Diary of a Wimpy Kid",
                    author = "Jeff Kinney",
                    year = 2007,
                    category = "Children's Humor",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Follow Greg Heffley, a middle school student, as he navigates the awkward world of adolescence through his hilarious diary entries and stick-figure drawings."
                ),
                BookEntity(
                    isbn = "9780810994737",
                    title = "Diary of a Wimpy Kid: Rodrick Rules",
                    author = "Jeff Kinney",
                    year = 2008,
                    category = "Children's Humor",
                    coverImageRes = android.R.drawable.ic_menu_gallery,
                    description = "Greg's older brother Rodrick knows Greg's most embarrassing secret, and Greg has to deal with the consequences while trying to make it through another school year."
                )
            )
            repository.insertBooks(sampleBooks)
        }
    }
}
package au.edu.curtin.madassignment2

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class BooksViewModel : ViewModel() {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api = retrofit.create(OpenLibraryApi::class.java)

    private val _state = MutableStateFlow(BooksUiState())
    val state: StateFlow<BooksUiState> = _state.asStateFlow()

    fun onSearch(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(allBooks = emptyList(), searchQuery = "") }
            return
        }
        fetchBooksFromApi(query)
    }

    private fun fetchBooksFromApi(query: String) {
        viewModelScope.launch {
            try {
                println("🔍 Searching for: $query")
                val response = api.searchBooks(query)
                println("📡 API Response: ${response.docs.size} docs")

                val books = response.docs.mapNotNull { book ->
                    if (book.key != null && book.title != null) {
                        BookEntity(
                            id = book.key,
                            title = book.title,
                            author = book.author_name?.joinToString(", ") ?: "Unknown Author",
                            year = book.first_publish_year ?: 0,
                            coverImageUrl = book.cover_i?.let {
                                "https://covers.openlibrary.org/b/id/$it-M.jpg"
                            },
                            category = "General"
                        )
                    } else null
                }

                println("✅ Mapped ${books.size} books successfully")

                _state.update {
                    it.copy(
                        allBooks = books,
                        searchQuery = query
                    )
                }
            } catch (e: Exception) {
                println("❌ Error: ${e.message}")
                e.printStackTrace()
                _state.update { it.copy(allBooks = emptyList(), searchQuery = query) }
            }
        }
    }

    fun onCategorySelected(category: String) =
        _state.update { it.copy(selectedCategory = category) }

    fun toggleFavoritesOnly() =
        _state.update { it.copy(showFavoritesOnly = !it.showFavoritesOnly) }

    fun toggleFavorite(id: String) = _state.update {
        val next = it.favorites.toMutableSet().apply {
            if (!add(id)) remove(id)
        }
        it.copy(favorites = next)
    }
}
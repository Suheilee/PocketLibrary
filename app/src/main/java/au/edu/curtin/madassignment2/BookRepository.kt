package au.edu.curtin.madassignment2

import kotlinx.coroutines.flow.Flow

class BookRepository(private val bookDao: BookDao) {

    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    fun getFavoriteBooks(): Flow<List<BookEntity>> = bookDao.getFavoriteBooks()

    fun getBooksByCategory(category: String): Flow<List<BookEntity>> =
        bookDao.getBooksByCategory(category)

    fun searchBooks(query: String): Flow<List<BookEntity>> =
        bookDao.searchBooks(query)

    fun getAllCategories(): Flow<List<String>> = bookDao.getAllCategories()

    suspend fun getBookByIsbn(isbn: String): BookEntity? =
        bookDao.getBookByIsbn(isbn)

    suspend fun insertBook(book: BookEntity) = bookDao.insertBook(book)

    suspend fun insertBooks(books: List<BookEntity>) = bookDao.insertBooks(books)

    suspend fun updateBook(book: BookEntity): Int = bookDao.updateBook(book)

    suspend fun deleteBook(book: BookEntity): Int = bookDao.deleteBook(book)

    suspend fun updateFavoriteStatus(isbn: String, isFavorite: Boolean): Int =
        bookDao.updateFavoriteStatus(isbn, isFavorite)

    suspend fun deleteAllBooks(): Int = bookDao.deleteAllBooks()
}
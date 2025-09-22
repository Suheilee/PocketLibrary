package au.edu.curtin.madassignment2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE is_favorite = 1")
    fun getAllFavorites(): Flow<List<BookEntity>>

    // Removed duplicate getFavoriteBooks()

    @Query("SELECT * FROM books WHERE category = :category")
    fun getBooksByCategory(category: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE title LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%'")
    fun searchBooks(searchQuery: String): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE is_favorite = 1 AND (title LIKE '%' || :searchQuery || '%' OR author LIKE '%' || :searchQuery || '%')")
    fun searchFavorites(searchQuery: String): Flow<List<BookEntity>>

    @Query("SELECT DISTINCT category FROM books ORDER BY category")
    fun getAllCategories(): Flow<List<String>>

    // Fetch by ID regardless of favorite status
    @Query("SELECT * FROM books WHERE id = :id")
    suspend fun getBookById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE isbn = :isbn")
    suspend fun getBookByIsbn(isbn: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity): Int

    @Delete
    suspend fun deleteBook(book: BookEntity): Int

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("UPDATE books SET is_favorite = :isFavorite WHERE isbn = :isbn")
    suspend fun updateFavoriteStatus(isbn: String, isFavorite: Boolean): Int

    @Query("DELETE FROM books")
    suspend fun deleteAllBooks(): Int
}
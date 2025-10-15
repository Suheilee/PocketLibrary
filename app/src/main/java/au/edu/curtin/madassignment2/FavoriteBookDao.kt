package au.edu.curtin.madassignment2

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteBookDao {
    @Query("SELECT * FROM favorite_books")
    fun getAllFavorites(): Flow<List<FavoriteBookEntity>>

    @Query("SELECT * FROM favorite_books WHERE id = :id")
    suspend fun getFavoriteById(id: String): FavoriteBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(book: FavoriteBookEntity)

    @Delete
    suspend fun deleteFavorite(book: FavoriteBookEntity)

    @Query("DELETE FROM favorite_books WHERE id = :id")
    suspend fun deleteFavoriteById(id: String)

    @Query("SELECT id FROM favorite_books")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("UPDATE favorite_books SET localCoverPhotoPath = :photoPath WHERE id = :bookId")
    suspend fun updateCoverPhoto(bookId: String, photoPath: String)
}
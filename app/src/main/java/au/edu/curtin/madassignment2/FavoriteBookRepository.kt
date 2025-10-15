package au.edu.curtin.madassignment2

import kotlinx.coroutines.flow.Flow

class FavoriteBookRepository(
    private val dao: FavoriteBookDao,
    private val firestoreService: FirestoreService
) {

    val allFavorites: Flow<List<FavoriteBookEntity>> = dao.getAllFavorites()
    val allFavoriteIds: Flow<List<String>> = dao.getAllFavoriteIds()

    suspend fun insertFavorite(book: FavoriteBookEntity) {
        dao.insertFavorite(book)
        // Sync to Firebase
        try {
            firestoreService.syncFavoriteBooks(listOf(book))
        } catch (e: Exception) {
            // Handle offline scenario - the sync will happen when back online
            e.printStackTrace()
        }
    }

    suspend fun deleteFavoriteById(id: String) {
        dao.deleteFavoriteById(id)
        // Note: You might want to also delete from Firestore
    }

    suspend fun isFavorite(id: String): Boolean {
        return dao.getFavoriteById(id) != null
    }

    suspend fun updateCoverPhoto(bookId: String, photoPath: String) {
        dao.updateCoverPhoto(bookId, photoPath)
        // Sync updated book to Firebase
        val updatedBook = dao.getFavoriteById(bookId)
        updatedBook?.let {
            firestoreService.syncFavoriteBooks(listOf(it))
        }
    }

    suspend fun getFavoriteById(id: String): FavoriteBookEntity? {
        return dao.getFavoriteById(id)
    }

    suspend fun syncWithCloud() {
        try {
            val cloudBooks = firestoreService.getFavoriteBooksFromCloud()
            cloudBooks.forEach { book ->
                dao.insertFavorite(book)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
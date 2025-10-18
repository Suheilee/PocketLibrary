package au.edu.curtin.madassignment2

import kotlinx.coroutines.flow.Flow
import java.io.File

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
        // Get the book first to delete its photo file
        val book = dao.getFavoriteById(id)
        book?.localCoverPhotoPath?.let { photoPath ->
            try {
                File(photoPath).delete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        dao.deleteFavoriteById(id)
        // Note: You might want to also delete from Firestore
    }

    suspend fun isFavorite(id: String): Boolean {
        return dao.getFavoriteById(id) != null
    }

    suspend fun updateCoverPhoto(bookId: String, photoPath: String) {
        try {
            dao.updateCoverPhoto(bookId, photoPath)
            // Sync updated book to Firebase
            val updatedBook = dao.getFavoriteById(bookId)
            updatedBook?.let {
                try {
                    firestoreService.syncFavoriteBooks(listOf(it))
                } catch (e: Exception) {
                    // Handle offline - will sync later
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    suspend fun removeCoverPhoto(bookId: String) {
        try {
            // Get the book to find the photo path
            val book = dao.getFavoriteById(bookId)

            // Delete the physical file
            book?.localCoverPhotoPath?.let { photoPath ->
                try {
                    val file = File(photoPath)
                    if (file.exists()) {
                        val deleted = file.delete()
                        println("DEBUG: File deletion ${if (deleted) "successful" else "failed"}: $photoPath")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Update database to remove the path
            dao.removeCoverPhoto(bookId)

            // Sync updated book to Firebase
            val updatedBook = dao.getFavoriteById(bookId)
            updatedBook?.let {
                try {
                    firestoreService.syncFavoriteBooks(listOf(it))
                } catch (e: Exception) {
                    // Handle offline - will sync later
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
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
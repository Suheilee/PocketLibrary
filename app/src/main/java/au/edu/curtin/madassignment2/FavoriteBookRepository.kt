package au.edu.curtin.madassignment2

import kotlinx.coroutines.flow.Flow

class FavoriteBookRepository(private val dao: FavoriteBookDao) {

    val allFavorites: Flow<List<FavoriteBookEntity>> = dao.getAllFavorites()

    val allFavoriteIds: Flow<List<String>> = dao.getAllFavoriteIds()

    suspend fun insertFavorite(book: FavoriteBookEntity) {
        dao.insertFavorite(book)
    }

    suspend fun deleteFavoriteById(id: String) {
        dao.deleteFavoriteById(id)
    }

    suspend fun isFavorite(id: String): Boolean {
        return dao.getFavoriteById(id) != null
    }
}
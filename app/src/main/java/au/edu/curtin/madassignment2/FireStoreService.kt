package au.edu.curtin.madassignment2

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreService {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private fun usersCollection() = db.collection("users")

    suspend fun saveUserData(user: UserEntity) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            usersCollection().document(currentUser.uid).set(user).await()
        }
    }

    suspend fun getUserData(): UserEntity? {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            try {
                val document = usersCollection().document(currentUser.uid).get().await()
                if (document.exists()) {
                    document.toObject(UserEntity::class.java)
                } else {
                    // Create new user document if it doesn't exist
                    val newUser = UserEntity(
                        userId = currentUser.uid,
                        email = currentUser.email,
                        displayName = currentUser.displayName
                    )
                    saveUserData(newUser)
                    newUser
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    suspend fun syncFavoriteBooks(favoriteBooks: List<FavoriteBookEntity>) {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userDoc = usersCollection().document(currentUser.uid)

            // Sync each favorite book to Firestore
            favoriteBooks.forEach { book ->
                db.collection("users").document(currentUser.uid)
                    .collection("favorite_books")
                    .document(book.id)
                    .set(book)
                    .await()
            }

            // Update user's last sync time
            userDoc.update("lastSync", System.currentTimeMillis()).await()
        }
    }

    suspend fun getFavoriteBooksFromCloud(): List<FavoriteBookEntity> {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            try {
                val snapshot = db.collection("users").document(currentUser.uid)
                    .collection("favorite_books")
                    .get()
                    .await()
                snapshot.toObjects(FavoriteBookEntity::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }
}
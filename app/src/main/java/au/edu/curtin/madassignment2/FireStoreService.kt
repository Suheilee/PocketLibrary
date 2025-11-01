package au.edu.curtin.madassignment2

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class FirestoreService {
    val db = Firebase.firestore
    val auth = Firebase.auth

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

//    suspend fun syncFavoriteBooks(favoriteBooks: List<FavoriteBookEntity>) {
//        val currentUser = auth.currentUser
//        if (currentUser != null) {
//            val userDoc = usersCollection().document(currentUser.uid)
//
//            // Sync each favorite book to Firestore
//            favoriteBooks.forEach { book ->
//                db.collection("users").document(currentUser.uid)
//                    .collection("favorite_books")
//                    .document(book.id)
//                    .set(book)
//                    .await()
//            }
//
//            // Update user's last sync time
//            userDoc.update("lastSync", System.currentTimeMillis()).await()
//        }
//    }

    suspend fun syncFavoriteBooks(favoriteBooks: List<FavoriteBookEntity>) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("FirestoreService", "Cannot sync books: user is null")
            return
        }

        val userDoc = usersCollection().document(currentUser.uid)

        favoriteBooks.forEach { book ->
            if (book.id.isBlank()) {
                Log.e("FirestoreService", "Book ID is blank, skipping: $book")
                return@forEach
            }

            // Encode the ID to be URL-safe for the document path only
            val safeId = URLEncoder.encode(book.id, StandardCharsets.UTF_8.toString())

            try {
                // Use safeId as the document path, but keep the original book object (so its id field stays original)
                db.collection("users")
                    .document(currentUser.uid)
                    .collection("favorite_books")
                    .document(safeId)
                    .set(book) // store the original id in the document data
                    .await()
                Log.d("FirestoreService", "Book synced successfully: ${book.title} (docId: $safeId)")
            } catch (e: Exception) {
                Log.e("FirestoreService", "Failed to sync book: ${book.id}", e)
            }
        }

        // Update last sync time
        try {
            userDoc.update("lastSync", System.currentTimeMillis()).await()
            Log.d("FirestoreService", "Last sync time updated.")
        } catch (e: Exception) {
            Log.e("FirestoreService", "Failed to update last sync time", e)
        }
    }

//    suspend fun getFavoriteBooksFromCloud(): List<FavoriteBookEntity> {
//        val currentUser = auth.currentUser
//
//        return if (currentUser != null) {
//            try {
//                val snapshot = db.collection("users")
//                    .document(currentUser.uid)
//                    .collection("favorite_books")
//                    .get()
//                    .await()
//                return snapshot.toObjects(FavoriteBookEntity::class.java)
//            } catch (e: Exception) {
//                emptyList()
//            }
//        } else {
//            emptyList()
//        }
//    }

    suspend fun getFavoriteBooksFromCloud(): List<FavoriteBookEntity> {
        val user = auth.currentUser
        println("User UID = ${user?.uid}")
        println("Reading from path = users/${user?.uid}/favorite_books")

        if (user == null) return emptyList()

        val snapshot = db.collection("users")
            .document(user.uid)
            .collection("favorite_books")
            .get()
            .await()

        println("Cloud returned ${snapshot.size()} books")
        snapshot.documents.forEach {
            println("Found: ${it.id} => ${it.data}")
        }

//        return snapshot.toObjects(FavoriteBookEntity::class.java)
        return snapshot.documents.mapNotNull { doc ->
            val book = doc.toObject(FavoriteBookEntity::class.java)
            // Decode the ID back from the Firestore-safe format
            book?.copy(id = java.net.URLDecoder.decode(doc.id, java.nio.charset.StandardCharsets.UTF_8.toString()))
        }
    }

}
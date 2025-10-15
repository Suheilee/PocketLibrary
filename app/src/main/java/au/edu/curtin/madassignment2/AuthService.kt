package au.edu.curtin.madassignment2

import android.content.Context
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService(private val context: Context) {

    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Boolean> {
        return try {
            // Using the auth instance directly to avoid confusion
            val auth = Firebase.auth
            val result = auth.signInWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                println("DEBUG: User signed in: ${result.user?.email}")
                Result.success(true)
            } else {
                Result.failure(Exception("No user returned"))
            }
        } catch (e: Exception) {
            println("DEBUG: Sign in error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<Boolean> {
        return try {
            val auth = Firebase.auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                println("DEBUG: User created: ${result.user?.email}")
                Result.success(true)
            } else {
                Result.failure(Exception("No user returned"))
            }
        } catch (e: Exception) {
            println("DEBUG: Create user error: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<Boolean> {
        return try {
            val auth = Firebase.auth
            val result = auth.signInAnonymously().await()

            if (result.user != null) {
                println("DEBUG: Anonymous user: ${result.user?.uid}")
                Result.success(true)
            } else {
                Result.failure(Exception("No user returned"))
            }
        } catch (e: Exception) {
            println("DEBUG: Anonymous error: ${e.message}")
            Result.failure(e)
        }
    }

    fun signOut() {
        Firebase.auth.signOut()
        println("DEBUG: Signed out")
    }

    val currentUser get() = Firebase.auth.currentUser
    val isUserLoggedIn get() = Firebase.auth.currentUser != null
    fun getCurrentUserId() = Firebase.auth.currentUser?.uid
    fun getCurrentUserEmail() = Firebase.auth.currentUser?.email
}
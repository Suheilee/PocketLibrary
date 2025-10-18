package au.edu.curtin.madassignment2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val isLoggedIn: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentUser: UserEntity? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    // Lazy init so FirebaseAuth is not called before Firebase is ready
    private val authService: AuthService by lazy { AuthService(application.applicationContext) }
    private val firestoreService: FirestoreService by lazy { FirestoreService() }

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState

    init {
        viewModelScope.launch {
            checkCurrentUser()
        }
    }

    private suspend fun checkCurrentUser() {
        _authState.value = _authState.value.copy(
            isLoggedIn = authService.isUserLoggedIn
        )
    }

    fun signIn(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authService.signInWithEmailAndPassword(email, password)
            if (result.isSuccess) {
                syncUserData()
                _authState.value = AuthState(isLoggedIn = true)
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Sign in failed"
                )
            }
        }
    }

    fun createUser(email: String, password: String) {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authService.createUserWithEmailAndPassword(email, password)
            if (result.isSuccess) {
                syncUserData()
                _authState.value = AuthState(isLoggedIn = true)
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Account creation failed"
                )
            }
        }
    }

    fun signInAnonymously() {
        _authState.value = _authState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = authService.signInAnonymously()
            if (result.isSuccess) {
                syncUserData()
                _authState.value = AuthState(isLoggedIn = true)
            } else {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = result.exceptionOrNull()?.message ?: "Anonymous sign in failed"
                )
            }
        }
    }

    fun signOut() {
        authService.signOut()
        _authState.value = AuthState()
    }

    private suspend fun syncUserData() {
        try {
            val userData = firestoreService.getUserData()
            _authState.value = _authState.value.copy(currentUser = userData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

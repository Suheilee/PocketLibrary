package au.edu.curtin.madassignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.google.firebase.FirebaseApp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

class MainActivity : ComponentActivity() {
    // Use the AndroidX lifecycle delegate for AndroidViewModel
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            PocketLibraryTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                // ✅ Use the one we created above, not Compose's viewModel()
                val viewModel = authViewModel

                LaunchedEffect(viewModel.authState.collectAsState().value.isLoggedIn) {
                    isLoggedIn = viewModel.authState.value.isLoggedIn
                }

                if (isLoggedIn) {
                    MainScreen()
                } else {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

package au.edu.curtin.madassignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PocketLibraryTheme {
//                 Track login state
                var isLoggedIn by remember { mutableStateOf(false) }

                // Safe ViewModel initialization
                val authViewModel: AuthViewModel = viewModel()

                // Collect auth state safely
                val authState by authViewModel.authState.collectAsState()

                // Update login state when authState changes
                LaunchedEffect(authState.isLoggedIn) {
                    isLoggedIn = authState.isLoggedIn
                }

                if (isLoggedIn) {
                    MainScreen()
                } else {
                    LoginScreen(
                        onLoginSuccess = { isLoggedIn = true },
                        viewModel = authViewModel
                    )
                }
            }
        }
    }
}

package au.edu.curtin.madassignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.CreamBackground
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme
import au.edu.curtin.madassignment2.ui.theme.WarmOrange

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PocketLibraryTheme {
                Scaffold(
                    containerColor = CreamBackground
                ) { innerPadding ->
                    MainWindow(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


// Main window of the app
@Preview(showBackground = true)
@Composable
fun MainWindow(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            Row {
                AppTitle()
            }
            Row {
                SearchBar()
            }
        }
    }
}

// Title of the app
@Composable
fun AppTitle() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "My Shelf",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun SearchBar() {
    Text(
        text = "Search Bar"
    )
}
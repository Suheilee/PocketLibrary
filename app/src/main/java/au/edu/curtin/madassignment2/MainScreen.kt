package au.edu.curtin.madassignment2

import PocketLibraryTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MainScreen() {
    Column {
        // App title
        Row (
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.Center
        ) {
            Text (
                text = "Pocket Library",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // App tagline
        Row (
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Your small pocket library",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Search bar
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            horizontalArrangement = Arrangement.Center
        ) {
            SearchBarPreview()
        }

        Spacer(modifier = Modifier.width(20.dp))


        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            horizontalArrangement = Arrangement.Center
        ) {
            Column (
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                sampleBooks.forEach { book ->
                    BookCard(book = book)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PocketLibraryTheme {
        MainScreen()
    }
}

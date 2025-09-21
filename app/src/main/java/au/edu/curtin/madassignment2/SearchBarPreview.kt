package au.edu.curtin.madassignment2

import PocketLibraryTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun SearchBar() {
    Text(text = "Search Bar")
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    PocketLibraryTheme {
        SearchBar()
    }
}

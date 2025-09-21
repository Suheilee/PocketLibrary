package au.edu.curtin.madassignment2

import PocketLibraryTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun BookCard(book: Book) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Book cover
            Row (
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center
            ){
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Book cover",
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Book title and year
            Row (
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Text(
                        text = book.title
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                Column {
                    Text(
                        text = "(${book.year})"
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            // Author
            Row (
                modifier = Modifier
                    .fillMaxWidth(),

                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                text = book.author
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookcardPreview() {
    PocketLibraryTheme {
        BookCard(
            book = Book(
                isbn = "9780451524935",
                title = "1984",
                author = "George Orwell",
                year = 1949,
                coverImage = null
            )
        )
    }
}
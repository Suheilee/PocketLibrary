package au.edu.curtin.madassignment2

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun BookCard(
    book: BookEntity,
    onBookClick: (BookEntity) -> Unit = {},
    onFavoriteClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .clickable { onBookClick(book) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Book cover image
            if (book.coverImageRes != null) {
                Image(
                    painter = painterResource(id = book.coverImageRes),
                    contentDescription = "Book cover for ${book.title}",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Default placeholder
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                    contentDescription = "Book cover for ${book.title}",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "(${book.year})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.category,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = { onFavoriteClick(book.id) }, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (book.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (book.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookCardPreview() {
    PocketLibraryTheme {
        BookCard(
            book = BookEntity(
                id = "1",
                title = "Harry Potter and the Philosopher's Stone",
                author = "J.K. Rowling",
                year = 1997,
                category = "Fantasy",
                coverImageRes = android.R.drawable.ic_menu_gallery,
                isFavorite = true
            )
        )
    }
}
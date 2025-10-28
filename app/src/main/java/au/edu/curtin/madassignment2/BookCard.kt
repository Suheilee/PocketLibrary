package au.edu.curtin.madassignment2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme
import coil.compose.AsyncImage
import java.io.File

@Composable
fun BookCard(
    book: BookEntity,
    onFavoriteClick: (String) -> Unit = {},
    onShareClick: (BookEntity) -> Unit = {},
    onCameraClick: (String) -> Unit = {},
    onRemoveCoverClick: (String) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Book cover - prioritize local photo over URL
            when {
                book.localCoverPhotoPath != null && File(book.localCoverPhotoPath).exists() -> {
                    AsyncImage(
                        model = File(book.localCoverPhotoPath),
                        contentDescription = "Personal cover for ${book.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                book.coverImageUrl != null -> {
                    AsyncImage(
                        model = book.coverImageUrl,
                        contentDescription = "Book cover for ${book.title}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Image(
                        painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                        contentDescription = "Default cover",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = book.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = book.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "(${book.year}) • ${book.category}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            // Action buttons row
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Favorite button
                IconButton(onClick = { onFavoriteClick(book.id) }) {
                    Icon(
                        imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (book.isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (book.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                // Share button
                IconButton(onClick = { onShareClick(book) }) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share book details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                // Camera button - only show for favorites
                if (book.isFavorite) {
                    IconButton(onClick = { onCameraClick(book.id) }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = "Take cover photo",
                            tint = if (book.localCoverPhotoPath != null)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Remove cover button - only show if there's a custom cover
                if (book.isFavorite && book.localCoverPhotoPath != null) {
                    IconButton(onClick = { onRemoveCoverClick(book.id) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove cover photo",
                            tint = MaterialTheme.colorScheme.error
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
                isFavorite = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookCardWithPhotoPreview() {
    PocketLibraryTheme {
        BookCard(
            book = BookEntity(
                id = "1",
                title = "Harry Potter and the Philosopher's Stone",
                author = "J.K. Rowling",
                year = 1997,
                category = "Fantasy",
                isFavorite = true,
                localCoverPhotoPath = "/path/to/photo.jpg"
            )
        )
    }
}
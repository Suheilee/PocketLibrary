package au.edu.curtin.madassignment2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun BookDetailScreen(
    book: BookEntity,
    onBackClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onFavoriteToggle: (String) -> Unit = {}
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Row {
                    IconButton(onClick = onShareClick) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { onFavoriteToggle(book.id) }) {
                        Icon(
                            imageVector = if (book.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (book.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (book.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    if (book.coverImageRes != null) {
                        Image(
                            painter = painterResource(id = book.coverImageRes),
                            contentDescription = "Book cover for ${book.title}",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
                            contentDescription = "Book cover for ${book.title}",
                            modifier = Modifier.size(120.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Top) {
                        Text(
                            text = book.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "by ${book.author}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "(${book.year})",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (book.isFavorite) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "♥ Favorite",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Category",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(text = book.category, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookDetailScreenPreview() {
    PocketLibraryTheme {
        BookDetailScreen(
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
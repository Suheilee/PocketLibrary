package au.edu.curtin.madassignment2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import au.edu.curtin.madassignment2.ui.theme.PocketLibraryTheme

@Composable
fun ManualBookEntryDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, author: String, year: Int, category: String) -> Unit,
    availableCategories: List<String> = listOf("All", "Fantasy", "Romance", "Classic", "Mystery", "General")
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var yearText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("General") }
    var titleError by remember { mutableStateOf(false) }
    var authorError by remember { mutableStateOf(false) }
    var yearError by remember { mutableStateOf(false) }
    var showCategoryDialog by remember { mutableStateOf(false) }

    // Filter out "All" from categories for selection
    val selectableCategories = availableCategories.filter { it != "All" }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Book",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Add Book Manually",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title field
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Book Title",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            titleError = false
                        },
                        placeholder = { Text("Enter book title") },
                        isError = titleError,
                        supportingText = {
                            if (titleError) {
                                Text(
                                    text = "Title is required",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Author field
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Author",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = author,
                        onValueChange = {
                            author = it
                            authorError = false
                        },
                        placeholder = { Text("Enter author name") },
                        isError = authorError,
                        supportingText = {
                            if (authorError) {
                                Text(
                                    text = "Author is required",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Year field
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Publication Year",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    OutlinedTextField(
                        value = yearText,
                        onValueChange = {
                            // Only allow digits
                            if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                                yearText = it
                                yearError = false
                            }
                        },
                        placeholder = { Text("e.g., 2020") },
                        isError = yearError,
                        supportingText = {
                            if (yearError) {
                                Text(
                                    text = "Valid year is required (e.g., 2020)",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Category selection button
                OutlinedButton(
                    onClick = { showCategoryDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = selectedCategory,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate inputs
                    var hasError = false

                    if (title.isBlank()) {
                        titleError = true
                        hasError = true
                    }

                    if (author.isBlank()) {
                        authorError = true
                        hasError = true
                    }

                    val year = yearText.toIntOrNull()
                    if (year == null || year < 1000 || year > 2100) {
                        yearError = true
                        hasError = true
                    }

                    if (!hasError && year != null) {
                        onConfirm(title.trim(), author.trim(), year, selectedCategory)
                    }
                }
            ) {
                Text("Add Book")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    // Category selection dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectableCategories.forEach { category ->
                        OutlinedButton(
                            onClick = {
                                selectedCategory = category
                                showCategoryDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = if (selectedCategory == category) {
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            } else {
                                ButtonDefaults.outlinedButtonColors()
                            }
                        ) {
                            Text(category)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ManualBookEntryDialogPreview() {
    PocketLibraryTheme {
        ManualBookEntryDialog(
            onDismiss = {},
            onConfirm = { _, _, _, _ -> }
        )
    }
}
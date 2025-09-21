package au.edu.curtin.madassignment2

data class Book(
    val isbn: String,
    val title: String,
    val author: String,
    val year: Int,
    val coverImage: String?
)
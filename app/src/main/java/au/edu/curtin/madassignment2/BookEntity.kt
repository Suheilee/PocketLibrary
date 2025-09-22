package au.edu.curtin.madassignment2

data class BookEntity(
    val id: String,
    val isbn: String = "",
    val title: String,
    val author: String,
    val year: Int,
    val category: String = "General",
    val coverImageUrl: String? = null,
    val coverImageRes: Int? = null,
    val description: String = "",
    val isFavorite: Boolean = false
)
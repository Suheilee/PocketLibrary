package au.edu.curtin.madassignment2

data class OpenLibraryResponse(
    val docs: List<OpenBookDoc>
)

data class OpenBookDoc(
    val key: String?,
    val title: String?,
    val author_name: List<String>?,
    val first_publish_year: Int?,
    val cover_i: Int?
)
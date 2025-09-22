package au.edu.curtin.madassignment2

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("fields") fields: String = "key,title,author_name,first_publish_year,cover_i",
        @Query("limit") limit: Int = 20
    ): OpenLibraryResponse
}

@JsonClass(generateAdapter = true)
data class OpenLibraryResponse(
    @Json(name = "docs") val docs: List<OpenLibraryBook>? = null,
    @Json(name = "numFound") val numFound: Int = 0
)

@JsonClass(generateAdapter = true)
data class OpenLibraryBook(
    @Json(name = "key") val key: String,
    @Json(name = "title") val title: String? = null,
    @Json(name = "author_name") val authorName: List<String>? = null,
    @Json(name = "first_publish_year") val firstPublishYear: Int? = null,
    @Json(name = "cover_i") val coverId: Int? = null
)

// Extension function to convert OpenLibraryBook to BookEntity
fun OpenLibraryBook.toBookEntity(): BookEntity {
    val author = authorName?.joinToString(", ") ?: "Unknown Author"
    val year = firstPublishYear ?: 2024
    val coverImageUrl = coverId?.let {
        "https://covers.openlibrary.org/b/id/$it-S.jpg"
    }

    return BookEntity(
        id = key,
        title = title ?: "Unknown Title",
        author = author,
        year = year,
        coverImageUrl = coverImageUrl,
        isFavorite = false
    )
}
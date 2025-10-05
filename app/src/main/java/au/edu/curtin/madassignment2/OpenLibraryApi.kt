package au.edu.curtin.madassignment2

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json?fields=key,title,author_name,first_publish_year,cover_i&limit=20")
    suspend fun searchBooks(
        @Query("q") query: String
    ): OpenLibraryResponse
}
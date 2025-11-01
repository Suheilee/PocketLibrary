package au.edu.curtin.madassignment2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class FavoriteBookEntity(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val year: Int = 0,
    val category: String = "",
    val coverImageUrl: String? = null,
    val localCoverPhotoPath: String? = null
)

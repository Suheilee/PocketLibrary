package au.edu.curtin.madassignment2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String, // Open Library key or generated ID
    @ColumnInfo(name = "isbn") val isbn: String = "",
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "year") val year: Int,
    @ColumnInfo(name = "category") val category: String = "General",
    @ColumnInfo(name = "cover_image_url") val coverImageUrl: String? = null,
    @ColumnInfo(name = "cover_image_res") val coverImageRes: Int? = null,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "personal_photo_path") val personalPhotoPath: String? = null
)
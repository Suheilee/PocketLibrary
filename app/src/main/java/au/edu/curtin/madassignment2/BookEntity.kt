package au.edu.curtin.madassignment2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val isbn: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "author")
    val author: String,

    @ColumnInfo(name = "year")
    val year: Int,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "cover_image")
    val coverImageRes: Int,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
)
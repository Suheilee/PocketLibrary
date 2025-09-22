package au.edu.curtin.madassignment2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PocketLibraryDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao

    companion object {
        @Volatile
        private var INSTANCE: PocketLibraryDatabase? = null

        fun getDatabase(context: Context): PocketLibraryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PocketLibraryDatabase::class.java,
                    "pocket_library_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
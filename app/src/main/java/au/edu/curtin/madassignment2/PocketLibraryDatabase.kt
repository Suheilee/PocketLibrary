package au.edu.curtin.madassignment2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BookEntity::class],
    version = 2, // bumped from 1 -> 2 to avoid schema mismatch crash
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
                )
                    // If schema changes during development, wipe & rebuild DB to prevent startup crash
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
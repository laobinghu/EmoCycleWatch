package top.eaverse.emocycle.watch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MoodLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodLogDao(): MoodLogDao
}

package top.eaverse.emocycle.watch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MoodLogDao {
    @Insert
    suspend fun insert(log: MoodLogEntity): Long

    @Query("SELECT * FROM mood_logs ORDER BY createdAt DESC LIMIT :limit")
    suspend fun latest(limit: Int): List<MoodLogEntity>
}

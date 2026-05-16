package top.eaverse.emocycle.watch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_logs")
data class MoodLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val createdAt: Long,
    val moodScore: Int,
    val phase: String,
    val triggers: String,
    val note: String?
)

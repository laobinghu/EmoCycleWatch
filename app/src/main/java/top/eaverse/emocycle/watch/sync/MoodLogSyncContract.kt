package top.eaverse.emocycle.watch.sync

import top.eaverse.emocycle.watch.model.MoodPhase
import top.eaverse.emocycle.watch.model.Trigger

data class MoodLogSyncRecord(
    val id: Long,
    val createdAt: Long,
    val moodScore: Int,
    val phase: MoodPhase,
    val triggers: List<Trigger>,
    val note: String?
)

data class MoodLogSyncBatch(
    val schemaVersion: Int = 1,
    val records: List<MoodLogSyncRecord>
)

interface MoodLogSyncGateway {
    suspend fun push(batch: MoodLogSyncBatch)
    suspend fun pullSince(cursor: Long): MoodLogSyncBatch
}

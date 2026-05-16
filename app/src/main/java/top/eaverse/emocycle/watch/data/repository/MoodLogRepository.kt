package top.eaverse.emocycle.watch.data.repository

import top.eaverse.emocycle.watch.data.local.MoodLogDao
import top.eaverse.emocycle.watch.data.local.MoodLogEntity

class MoodLogRepository(
    private val dao: MoodLogDao
) {
    suspend fun insert(log: MoodLogEntity): Long = dao.insert(log)

    suspend fun latest(limit: Int): List<MoodLogEntity> = dao.latest(limit)
}

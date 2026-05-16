package top.eaverse.emocycle.watch.sync

import top.eaverse.emocycle.watch.data.local.MoodLogEntity

interface MoodLogSyncer {
    suspend fun sync(log: MoodLogEntity)
}

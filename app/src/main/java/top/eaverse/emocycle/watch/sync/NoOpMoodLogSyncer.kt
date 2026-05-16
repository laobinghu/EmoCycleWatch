package top.eaverse.emocycle.watch.sync

import top.eaverse.emocycle.watch.data.local.MoodLogEntity

class NoOpMoodLogSyncer : MoodLogSyncer {
    override suspend fun sync(log: MoodLogEntity) {
        // no-op in MVP
    }
}
